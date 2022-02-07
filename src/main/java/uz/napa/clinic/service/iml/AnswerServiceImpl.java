package uz.napa.clinic.service.iml;

import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uz.napa.clinic.entity.Answer;
import uz.napa.clinic.entity.Application;
import uz.napa.clinic.entity.Document;
import uz.napa.clinic.entity.enums.AnswerStatus;
import uz.napa.clinic.entity.enums.ApplicationStatus;
import uz.napa.clinic.entity.enums.DocumentStatus;
import uz.napa.clinic.exception.BadRequestException;
import uz.napa.clinic.payload.AnswerRequest;
import uz.napa.clinic.payload.AnswerResponse;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.repository.*;
import uz.napa.clinic.service.AnswerService;
import uz.napa.clinic.service.iml.helper.HtmlConverter;
import uz.napa.clinic.service.iml.helper.SmsSender;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnswerServiceImpl implements AnswerService {
    final
    ApplicationServiceImpl applicationService;
    final
    UserRepository userRepository;
    final
    AnswerRepository answerRepository;
    private final AttachmentRepository attachmentRepository;
    private final ApplicationRepository applicationRepository;
    private final DocumentRepository documentRepository;
    private final JavaMailSender mailSender;
    private final EskizServise eskizServise;



    public AnswerServiceImpl(@Lazy ApplicationServiceImpl applicationService, UserRepository userRepository, AnswerRepository answerRepository, AttachmentRepository attachmentRepository, ApplicationRepository applicationRepository, DocumentRepository documentRepository, JavaMailSender mailSender, EskizServise eskizServise) {
        this.applicationService = applicationService;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.attachmentRepository = attachmentRepository;
        this.applicationRepository = applicationRepository;
        this.documentRepository = documentRepository;
        this.mailSender = mailSender;
        this.eskizServise = eskizServise;
    }


    @Override
    public ApiResponse create(AnswerRequest request, UUID applicationId) {
        Answer savedAnswer = answerRepository.save(fillAnswer(new Answer(), request));
        Optional<Application> currentApplication = applicationRepository.findById(applicationId);
        if (currentApplication.isPresent()) {
            Document findDocument = documentRepository.findByApplicationAndDeletedFalseAndStatus(currentApplication.get(), DocumentStatus.INPROCESS);
            if (findDocument != null) {
                findDocument.setAnswer(savedAnswer);
                documentRepository.save(findDocument);
                sendBoss(savedAnswer);
                return new ApiResponse("Answer created!", true);
            } else {
                throw new BadRequestException("Document not found With Application ID: " + applicationId);
            }

        } else {
            throw new BadRequestException("Application not found with ID :" + applicationId);
        }
    }

    @Override
    public ApiResponse update(AnswerRequest request, UUID id) {
        Optional<Answer> findAnswer = answerRepository.findById(id);
        if (findAnswer.isPresent()) {
            answerRepository.save(fillAnswer(findAnswer.get(), request));
            return new ApiResponse("Successfully updated", true);
        } else {
            throw new BadRequestException("Answer not found By ID: " + id);
        }
    }

    @Override
    public ApiResponse updateWithDocument(AnswerRequest request, UUID documentId) {
        Optional<Document> byId = documentRepository.findById(documentId);
        if (byId.isPresent()) {
            Document document = byId.get();
            Optional<Answer> findAnswer = answerRepository.findById(document.getAnswer().getId());
            if (findAnswer.isPresent()) {
                Answer answer = fillAnswer(findAnswer.get(), request);
                answer.setStatus(AnswerStatus.CREATED);
                answerRepository.save(answer);
                document.setStatus(DocumentStatus.INPROCESS);
                documentRepository.save(document);
                return new ApiResponse("Successfully updated", true);
            } else {
                throw new BadRequestException("Answer not found With Document ID: " + documentId);
            }
        } else {
            throw new BadRequestException("Document not found By ID: " + documentId);
        }

    }

    @Override
    public AnswerResponse getOne(UUID id) {
        Optional<Answer> answer = answerRepository.findById(id);
        if (answer.isPresent()) {
            return AnswerResponse.fromEntity(answer.get());
        } else {
            throw new BadRequestException("Answer not found with ID: " + id);
        }
    }

    @Override
    public ApiResponse delete(UUID id) {
        Optional<Answer> findAnswer = answerRepository.findById(id);
        if (findAnswer.isPresent()) {
            Answer answer = findAnswer.get();
            answer.setDeleted(true);
            answerRepository.save(answer);
            return new ApiResponse("Answer deleted !", true);
        } else {
            throw new BadRequestException("Answer not found with ID: " + id);
        }
    }

    @Override
    public List<AnswerResponse> list(UUID id) {
        List<Answer> allByUserid = answerRepository.findByCreatedBy(id);
        return allByUserid.stream().map(this::getAnswer).collect(Collectors.toList());
    }

    @Override
    public ApiResponse sendBoss(UUID id) {
        Optional<Answer> findAnswer = answerRepository.findById(id);
        if (findAnswer.isPresent()) {
            Optional<Document> findDocument = documentRepository.findByAnswer(findAnswer.get());
            if (findDocument.isPresent()) {
                Document document = findDocument.get();
                document.setStatus(DocumentStatus.WAITING);
                Answer answer = findAnswer.get();
                answer.setStatus(AnswerStatus.WAITING);
                documentRepository.save(document);
                answerRepository.save(answer);
                return new ApiResponse("Answer sended to Boss", true);
            } else {
                throw new BadRequestException("Document not found by Answer ID: " + id);
            }
        } else {
            throw new BadRequestException("Answer not found with ID: " + id);
        }
    }

    public ApiResponse sendBoss(Answer answer) {
            Optional<Document> findDocument = documentRepository.findByAnswer(answer);
            if (findDocument.isPresent()) {
                Document document = findDocument.get();
                document.setStatus(DocumentStatus.WAITING);
                answer.setStatus(AnswerStatus.WAITING);
                documentRepository.save(document);
                answerRepository.save(answer);
                return new ApiResponse("Answer sended to Boss", true);
            } else {
                throw new BadRequestException("Document not found by Answer ID: ");
            }
    }

    @Override
    public ApiResponse sendApplicant(UUID id) {
        Optional<Answer> findAnswer = answerRepository.findById(id);
        if (findAnswer.isPresent()) {
            Optional<Document> byAnswer = documentRepository.findByAnswer(findAnswer.get());
            if (byAnswer.isPresent()) {
                Document document=byAnswer.get();
                document.setStatus(DocumentStatus.COMPLETED);
                document.getApplication().setStatus(ApplicationStatus.COMPLETED);
                Answer answer = findAnswer.get();
                answer.setDeniedMessage("");
                answer.setStatus(AnswerStatus.COMPLETED);
                answer.setLiked(true);
                answerRepository.save(answer);
                documentRepository.save(document);
                eskizServise.sendSms(byAnswer.get().getApplication().getCreatedBy().getPhoneNumber(),"Xurmatli mijos sizning murojatingiz" +
                        " ko'rib chiqildi. https://clinic.proacademy.uz/auth/login hovolasiga o'tib o'z hisobingiz orqali javobni ko'rishingiz mumkin!!!" +
                        "O'zbekiston Respublikasi Bosh prokraturasi Akademyasi Yuridik klinikasi");
                return new ApiResponse("Answer sended to Applicant", true);
            } else {
                throw new BadRequestException("Document not found by Answer ID: " + id);
            }

        } else {
            throw new BadRequestException("Answer not found with ID: " + id);
        }
    }

    public List<Answer> getByStatus(AnswerStatus status) {
        return answerRepository.findByStatusAndDeletedFalse(status);
    }

    public void sendEmail(String sendingEmail, String fullName) {
        String from = "uzproclinic@gmail.com";

//        try{
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(from);
//            message.setTo(sendingEmail);
//            message.setSubject("Qayta tiklash kaliti!!!");
//            message.setText(link);
//            mailSender.send(message);
//        }
        try {
            MimeMessage message1 = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message1);

            helper.setSubject("`Uz pro clinic` ariza ko'rib chiqildi");
            helper.setFrom(from);
            helper.setTo(sendingEmail);

            boolean html = true;
            helper.setText(HtmlConverter.convertCompleteHtml(fullName,"uz"), html);
            mailSender.send(message1);
        }
//        catch (Exception e) {
//
//        }
//
//        String body = "smth";
//        try {
//            String from1 = "islomxujanazarov0501@gmail.com";
//            MimeMessage message1 = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message1);
//            helper.setSubject("Reset Password");
//            helper.setFrom(from1);
//            helper.setTo(sendingEmail);
//            helper.setText(link, true);
//            mailSender.send(message);
//        }
        catch (Exception ignored) {
        }
    }

    public AnswerResponse getAnswer(Answer answer) {
        return AnswerResponse.fromEntity(answer);
    }

    private Answer fillAnswer(Answer answer, AnswerRequest request) {
        if (answer.getId() != null) {
            answer.setStatus(request.getStatus());
        } else {
            answer.setStatus(AnswerStatus.CREATED);
        }
        if (request.getDescription()!=null)answer.setDescription(request.getDescription());
        if (!request.getAttachmentId().isEmpty()) {
            answer.setAttachments(attachmentRepository.findAllById(request.getAttachmentId()));
        }
        if (request.getDeniedMessage() != null) {
            answer.setDeniedMessage(request.getDeniedMessage());
        }
        if (request.getComment() != null) {
            answer.setComment(request.getComment());
        }
        if (answer.isLiked() || !answer.isLiked()) {
            answer.setLiked(request.isLiked());
        }
        return answer;
    }
}

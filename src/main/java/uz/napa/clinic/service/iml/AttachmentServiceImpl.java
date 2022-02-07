package uz.napa.clinic.service.iml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.napa.clinic.entity.Attachment;
import uz.napa.clinic.entity.AttachmentContent;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.entity.enums.AttachStatus;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.AttachResponse;
import uz.napa.clinic.payload.ResPageable;
import uz.napa.clinic.repository.AttachmentContentRepository;
import uz.napa.clinic.repository.AttachmentRepository;
import uz.napa.clinic.repository.AttachmentTypeRepository;
import uz.napa.clinic.service.AttachmentService;
import uz.napa.clinic.utils.CommonUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    @Autowired
    AttachmentTypeRepository attachmentTypeRepository;
    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    @Value("${upload.folder}")
    private String uploadFolder;

//    public ApiResponse upload(MultipartFile file) {
//        return new ApiResponse();
//    }

//    public ApiResponse upload(MultipartHttpServletRequest request) throws IOException {
//        Iterator<String> fileNames = request.getFileNames();
//        MultipartFile file = request.getFile(fileNames.next());
//
//
//        if (!request.getParameter("type").equals("undefined")) {
//            AttachmentType attachmentType = attachmentTypeRepository.findByName(request.getParameter("type"));
//            if (!attachmentType.getContentType().contains(file.getContentType())) {
//                return new ApiResponse("File tipi " + attachmentType.getContentType() + " lardan biri bo'lishi kerak !", false);
//            }
//            Attachment attachment = new Attachment(
//                    file.getName(),
//                    file.getContentType(),
//                    file.getSize()
//            );
//            Attachment savedAttachment = attachmentRepository.save(attachment);
//            try {
//                AttachmentContent attachmentContent = new AttachmentContent(
//                        savedAttachment,
//                        file.getBytes()
//                );
//                attachmentContentRepository.save(attachmentContent);
//                return new ApiResponse("", true, savedAttachment.getId());
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//
//        }
//        return new ApiResponse("File type noma'lum", false);
//
//
//    }

    public HttpEntity<?> getFile(UUID id) {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("getAttachment"));
        AttachmentContent attachmentContent = attachmentContentRepository.findByAttachment(attachment);


        return ResponseEntity.ok().contentType(MediaType.valueOf(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getName() + "\"")
                .body(attachmentContent.getBytes());
    }

    public HttpEntity<?> getVideoFile(UUID id) throws MalformedURLException {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("getAttachment"));
//        AttachmentContent attachmentContent = attachmentContentRepository.findByAttachment(attachment);

        String contentType=attachment.getContentType().substring(0,attachment.getContentType().indexOf(";"));

        return ResponseEntity.ok().contentType(MediaType.valueOf(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getName() + "\"")
                .contentLength(attachment.getSize())
                .body(new FileUrlResource(String.format("%s/%s", uploadFolder, attachment.getUploadPath())));
    }

    public HttpEntity<?> getAudioFile(UUID id) throws MalformedURLException {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("getAttachment"));
        AttachmentContent attachmentContent = attachmentContentRepository.findByAttachment(attachment);


        return ResponseEntity.ok().contentType(MediaType.valueOf(attachment.getContentType().replace("*", "ogg")))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getName() + "\"")
                .contentLength(attachment.getSize())
                .body(new FileUrlResource(String.format("%s/%s", uploadFolder, attachment.getUploadPath())));
    }

    @Override
    public ResPageable getNormativeLegalBase(int page, int size) {
        Pageable pageable = CommonUtils.getPageable(page, size);
        Page<Attachment> attachments = attachmentRepository.findAllByStatusAndFileExtensionAndDeletedFalse(AttachStatus.FOR_NORMATIVE_LEGAL_BASE,"pdf", pageable);
        return new ResPageable(
                attachments.getContent().stream().map(attachment -> AttachResponse.toFront(attachment)).collect(Collectors.toList()),
                page,
                attachments.getTotalPages(),
                attachments.getTotalElements()
        );

    }


    @Override
    public ApiResponse uploadFile(MultipartHttpServletRequest request, User user) {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        Date date = new Date();
        File folder = new File(String.format("%s/%d/%d/%d", uploadFolder, 1900 + date.getYear(), 1 + date.getMonth(), date.getDate()));
        if (!folder.exists() && folder.mkdirs()) {
            System.out.println("folder created!!!");
        }

        Attachment attachment = new Attachment(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                getExt(file.getOriginalFilename()),
                uploadFolder,
                !user.getStatus().equals(UserStatus.ADMIN) ?
                        AttachStatus.FROM_THE_APPLICANT :
                        AttachStatus.FOR_NORMATIVE_LEGAL_BASE
        );
        attachment.setCreatedBy(user);
        attachment.setUpdatedBy(user);
        attachment.setCreatedAt(new Timestamp(new Date().getTime()));
        attachment.setUpdatedAt(new Timestamp(new Date().getTime()));
        Attachment savedAttachment = attachmentRepository.save(attachment);
        savedAttachment.setUploadPath(String.format("%d/%d/%d/%s.%s", 1900 + date.getYear(), 1 + date.getMonth(), date.getDate(),
                savedAttachment.getId(),
                savedAttachment.getFileExtension()
        ));
        attachmentRepository.save(savedAttachment);
        folder = folder.getAbsoluteFile();
        File file1 = new File(folder, String.format("%s.%s", savedAttachment.getId(), savedAttachment.getFileExtension()));
        try {
            file.transferTo(file1);
            return new ApiResponse("File saved!!!", true, savedAttachment.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return new ApiResponse("Error for saved!!!", true, savedAttachment.getId());
        }

    }

    @Override
    public HttpEntity<?> getFileById(UUID id) throws IOException {

        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("getAttachment"));
        Path path = Paths.get(attachment.getUploadPath());
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (attachment.getFileExtension().equals("pdf")) {

            File file = new File(String.format("%s/%s", uploadFolder, attachment.getUploadPath()));
            byte[] bytes = Files.readAllBytes(file.toPath());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getName() + "\"")
                    .contentLength(attachment.getSize())
                    .body(bytes);

        } else {

            FileUrlResource fileUrlResource = null;
            try {
                fileUrlResource = new FileUrlResource(String.format("%s/%s", uploadFolder, attachment.getUploadPath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getName() + "\"")
                    .body(fileUrlResource);
        }

    }

    private String getExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public ApiResponse delete(UUID id) {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new IllegalStateException("File not found for delete id!!!"));
        attachment.setDeleted(true);
        Attachment save = attachmentRepository.save(attachment);
        return new ApiResponse(save.isDeleted() ? "File deleted!!!" : "Error for deleted!!!", save.isDeleted());
    }

    public HttpEntity<?> getApplicantDoc() {
//        Resource resource = new ClassPathResource("/file/file.docx");
//
//        File file = new File(String.format("%s/%s", uploadFolder, attachment.getUploadPath()));
//
//        byte[] bytes = Files.readAllBytes(file.toPath());
//        return ResponseEntity.ok().contentType(MediaType.parseMediaType(attachment.getContentType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.docx")
//                .contentLength(attachment.getSize())
//                .body(bytes);

        return null;
    }
}

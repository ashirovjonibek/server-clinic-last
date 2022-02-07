package uz.napa.clinic.service.iml;

import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.napa.clinic.entity.Attachment;
import uz.napa.clinic.entity.Chat;
import uz.napa.clinic.entity.MessageCenter;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.entity.enums.AnswerStatus;
import uz.napa.clinic.entity.enums.AttachStatus;
import uz.napa.clinic.entity.enums.DocumentStatus;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.exception.BadRequestException;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.MessageHelper;
import uz.napa.clinic.payload.MessageResponse;
import uz.napa.clinic.payload.UserResponseForMessage;
import uz.napa.clinic.repository.*;
import uz.napa.clinic.service.MessageCenterService;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageCenterServiceImpl implements MessageCenterService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageCenterRepository messageCenterRepository;

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    DocumentRepository repository;

    @Autowired
    AttachmentTypeRepository attachmentTypeRepository;
    @Autowired
    AttachmentRepository attachmentRepository;

    @Value("${upload.folder}")
    private String uploadFolder;

    @Override
    public ApiResponse userMessages(User from, UUID chatId) {
        List<Chat> chats =
                from.getStatus().equals(UserStatus.APPLICANT) ? chatRepository.findAllByReceiverId(from.getId()) :
                        chatRepository.findByCreatorId(from.getId());
        if (chatId == null) {
            List<UserResponseForMessage> response = new ArrayList<>();
            for (int i = 0; i < chats.size(); i++) {
                response.add(
                        UserResponseForMessage.response(
                                from.getStatus().equals(UserStatus.APPLICANT) ?
                                        chats.get(i).getCreator() :
                                        chats.get(i).getReceiver(),
                                messageCenterRepository.findAllByChatIdAndToIdAndDeletedFalseAndReadFalse(chats.get(i).getId(),
                                        from.getId()
                                ).size(),
                                chats.get(i).getId(),
                                !from.getStatus().equals(UserStatus.APPLICANT) ?
                                        chats.get(i).getCreator().getId() :
                                        chats.get(i).getReceiver().getId()
                        ));
            }
            return new ApiResponse("Messages", true, response);
        } else {
            List<MessageCenter> messageCenterList = messageCenterRepository.findAllByChatIdAndDeletedFalseOrderByCreatedAt(chatId);
            for (MessageCenter message :
                    messageCenterList) {
                if (!from.getId().equals(message.getFrom().getId())) {
                    message.setRead(true);
                    messageCenterRepository.save(message);
                }
            }
            return new ApiResponse("Messages", true, messageCenterList.stream().
                    map(messageCenter -> MessageResponse.response(messageCenter)).collect(Collectors.toList())

            );
        }
    }

    @Override
    public ApiResponse userMessages(User from) {
        List<Chat> chats =
                from.getStatus().equals(UserStatus.APPLICANT) ? chatRepository.findAllByReceiverId(from.getId()) :
                        chatRepository.findByCreatorId(from.getId());
        List<UserResponseForMessage> response = new ArrayList<>();
        for (int i = 0; i < chats.size(); i++) {
            response.add(
                    UserResponseForMessage.response(
                            from.getStatus().equals(UserStatus.APPLICANT) ?
                                    chats.get(i).getCreator() :
                                    chats.get(i).getReceiver(),
                            messageCenterRepository.findAllByChatIdAndToIdAndDeletedFalseAndReadFalse(chats.get(i).getId(),
                                    from.getId()
                            ).size(),
                            chats.get(i).getId(),
                            !from.getStatus().equals(UserStatus.APPLICANT) ?
                                    chats.get(i).getCreator().getId() :
                                    chats.get(i).getReceiver().getId()
                    ));
        }
        return new ApiResponse("Messages", true, response);
    }

    @Override
    public ApiResponse sendOrEditMessage(MessageHelper message) {
        MessageCenter messageCenter = new MessageCenter();
        String restMessage;
        int status = 1;
        if (message.getMessageId() != null) {
            messageCenter = messageCenterRepository.findById(message.getMessageId()).orElseThrow(() -> new IllegalStateException("Message not found for edit message!!!"));
            if (messageCenter.getFrom().getId().equals(message.getFromId())) {
                messageCenter.setMessage(message.getMessage());
                messageCenter.setEdit(true);
                messageCenter.setUpdatedAt(new Timestamp(new Date().getTime()));
                messageCenter.setUpdatedBy(messageCenter.getCreatedBy());
                restMessage = "Message successfully edited!!!";
            } else {
                restMessage = "Edit method denied for this user!!!";
                status = 0;
            }
        } else {
            if (message.getChatId() != null) {
                messageCenter.setChat(chatRepository.findById(message.getChatId()).orElseThrow(() -> new IllegalStateException("Chat noot found")));
            } else {
                throw new BadRequestException("Chat not found!!!");
            }
            if (message.getAttachment()!=null) messageCenter.setAttachment(message.getAttachment());
            messageCenter.setEdit(false);
            messageCenter.setCreatedBy(userRepository.findById(message.getFromId()).orElseThrow(() -> new IllegalStateException("User not found for message!!!")));
            messageCenter.setFrom(userRepository.findById(message.getFromId()).orElseThrow(() -> new IllegalStateException("User not found for message!!!")));
            messageCenter.setCreatedAt(new Timestamp(new Date().getTime()));
            messageCenter.setUpdatedAt(new Timestamp(new Date().getTime()));
            messageCenter.setTo(userRepository.findById(message.getToId()).orElseThrow(() -> new IllegalStateException("User not found for message!!!")));
            messageCenter.setMessage(message.getMessage());

            restMessage = "Message saved successfully!!!";
        }

        if (status == 0) {
            return new ApiResponse(restMessage, false);
        } else {
            try {
                messageCenterRepository.save(messageCenter);
                return new ApiResponse(restMessage, true);
            } catch (Exception e) {
                e.printStackTrace();
                return new ApiResponse("Error for saved message!!!", false);
            }
        }
    }

    @Override
    public ApiResponse deleteMessage(UUID fromId, UUID messageId) {

        MessageCenter messageCenter = messageCenterRepository.findById(messageId).orElseThrow(() -> new IllegalStateException("Message not found for delete message!!!"));
        if (messageCenter.getFrom().getId().equals(fromId)) {
            messageCenter.setDeleted(true);
            try {
                messageCenterRepository.save(messageCenter);
                return new ApiResponse("Message deleted!!!", true);
            } catch (Exception e) {
                return new ApiResponse("Error for deleted!!!", false);
            }
        } else {
            return new ApiResponse("Permission denied for this user!!!", false);
        }
    }

    public UUID generateChat(User from, UUID toId) {
        User user = userRepository.findById(toId).orElseThrow(() -> new IllegalStateException("User not found"));
        if (chatRepository.existsByCreatorIdAndReceiverId(from.getId(), user.getId())) {
            return chatRepository.findByCreatorIdAndReceiverId(from.getId(), user.getId()).getId();
        } else
            return chatRepository.save(new Chat(from, user)).getId();

    }

    @Override
    public List<MessageResponse> findAll() {

        return messageCenterRepository.findAll().stream().map(messageCenter -> MessageResponse.response(messageCenter)).collect(Collectors.toList());
    }

    @Override
    public ApiResponse getCounts(User user) {
        Map<String, Integer> counter = new HashMap<>();

        if (user.getStatus().equals(UserStatus.APPLICANT)) {
            counter.put("complateApps", repository.findByCreatedByIdAndStatusAndDeletedFalse(user.getId(), DocumentStatus.COMPLETED).size());
            return new ApiResponse("ok", true, counter);
        } else if (user.getStatus().equals(UserStatus.LISTENER)) {
            counter.put("incoming", repository.findByCheckedByIdAndStatusAndDeletedFalse(user.getId(), DocumentStatus.CREATED).size() +
                    repository.findByCheckedByIdAndStatusAndAnswerIsNullAndDeletedFalse(user.getId(), DocumentStatus.INPROCESS).size()
            );
            counter.put("answer", repository.findByCheckedByIdAndAnswerStatusAndDeletedFalse(user.getId(), AnswerStatus.CREATED).size() +
                    repository.findByCheckedByIdAndAnswerStatusAndDeletedFalse(user.getId(), AnswerStatus.DENIED).size() +
                    repository.findByCheckedByIdAndAnswerStatusAndDeletedFalse(user.getId(), AnswerStatus.ACCEPTED).size());
            return new ApiResponse("ok", true, counter);
        } else if (user.getStatus().equals(UserStatus.MODERATOR)) {
            counter.put("setListener", repository.findByStatusAndSectionIdAndDeletedFalse(DocumentStatus.FORWARD_TO_MODERATOR, user.getSection().getId()).size());
            counter.put("checkAnswer", repository.findByStatusAndDeletedFalseAndCheckedBySectionOrderByCreatedAtDesc(DocumentStatus.WAITING,user.getSection()).size());
            return new ApiResponse("ok", true, counter);
        } else {
            counter.put("setSection", repository.findByStatusAndDeletedFalse(DocumentStatus.FORWARD_TO_SUPER_MODERATOR).size());
            return new ApiResponse("ok", true, counter);
        }
    }

    @Override
    public ApiResponse saveMessage(
            User user,
            MultipartFile file,
            String messageId,
            UUID fromId,
            String toId,
            String message,
            String chatId
    ) {

        if (file!=null){
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
                    AttachStatus.MESSAGE
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
            Attachment save = attachmentRepository.save(savedAttachment);
            folder = folder.getAbsoluteFile();
            File file1 = new File(folder, String.format("%s.%s", savedAttachment.getId(), savedAttachment.getFileExtension()));
            MessageHelper helper=new MessageHelper(
                    messageId!=null?UUID.fromString(messageId):null,
                    fromId,
                    UUID.fromString(toId),
                    message,
                    save,
                    UUID.fromString(chatId)
                    );
            try {
                file.transferTo(file1);
                return sendOrEditMessage(helper);
            } catch (IOException e) {
                e.printStackTrace();
                return new ApiResponse("Error for saved!!!", false);
            }
        }else {
            MessageHelper helper=new MessageHelper(
                    messageId!=null?UUID.fromString(messageId):null,
                    fromId,
                    toId!=null?UUID.fromString(toId):null,
                    message,
                    null,
                    chatId!=null?UUID.fromString(chatId):null
            );
            return sendOrEditMessage(helper);
        }

    }

    private String getExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}

package uz.napa.clinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.napa.clinic.entity.Document;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.entity.enums.DocumentStatus;
import uz.napa.clinic.payload.*;
import uz.napa.clinic.projection.CustomInfoCount;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    ApiResponse create(ApplicationRequest request);

    ApplicationResponse getOne(UUID id);

    ResPageable findAllByPageable(int page, int size,DocumentStatus status,User user);

    ApiResponse update(UUID id, ApplicationRequest request);

    ApiResponse delete(UUID id);

    List<ApplicationResponse> list();

    ResPageable getAllCheckedByListener(int page, int size, User user);

    ApiResponse changeListener(UUID applicationId, UUID userId);

    DocumentResponse getByApplication(UUID id);

    ResPageable getBossAnswers(User user, int page, int size);

    ResPageable getBossAnswers(User user, String search, int page, int size);

    ApiResponse confirmByBoss(UUID id);

    ApiResponse denied(UUID id, String comment);

    ResPageable deniedAnswerDocument(User user,Pageable pageable);

    ResPageable getCheckedApplication(int page, int size, User user);

    List<Document> findAll();

    ResPageable getAlllAcceptedDocument(Pageable pageable);

    ResPageable getAlllAcceptedApplication(User user, Pageable pageable);

    ResPageable getAllApplicationListenerIsNull(int page, int size,User user,String sts);

    ResPageable getAllDocumentToSend(User user, Pageable pageable);

    List<CustomInfoCount> getCountByCheckedBy();

    ResPageable getAnswerFeedback(User user, Pageable pageable);

    ApiResponse changeSection(UUID documentId, Long sectionId);

    ResPageable getAllDocs(String search, User user,DocumentStatus status, Pageable pageable);

}

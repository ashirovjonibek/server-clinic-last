package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.napa.clinic.entity.Document;
import uz.napa.clinic.entity.enums.DocumentStatus;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    private UUID id;
    private ApplicationResponse application;
    private AnswerResponse answer;
    private DocumentStatus status;
    private ListenerResponse checkedBy;
    private Long sectionId;
    private String forwardMessage;


    public static DocumentResponse fromEntity(Document document) {
        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setStatus(document.getStatus());
        response.setApplication(ApplicationResponse.fromEntity(document.getApplication()));
        if (document.getAnswer() != null) {
            response.setAnswer(AnswerResponse.fromEntity(document.getAnswer()));
        }
        if (document.getCheckedBy() != null) {
            response.setCheckedBy(ListenerResponse.fromEntity(document.getCheckedBy()));
        }
        if (document.getSection()!=null){
            response.setSectionId(document.getSection().getId());
        }
        if (document.getForwardMessage()!=null){
            response.setForwardMessage(document.getForwardMessage());
        }
        return response;
    }
}

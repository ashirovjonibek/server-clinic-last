package uz.napa.clinic.payload;

import lombok.*;
import uz.napa.clinic.entity.Answer;
import uz.napa.clinic.entity.enums.AnswerStatus;
import uz.napa.clinic.entity.template.BaseEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponse {
    private UUID id;
    private String description;
    private AnswerStatus status;
    private List<UUID> attachmentId;
    private String deniedMessage;
    private String comment;
    private boolean liked;

    public static AnswerResponse fromEntity(Answer answer) {
        AnswerResponse response = new AnswerResponse();
        response.setId(answer.getId());
        if (answer.getDescription() != null) {
            response.setDescription(answer.getDescription());
        }
        response.setStatus(answer.getStatus());
        if (!answer.getAttachments().isEmpty()) {
            response.setAttachmentId(answer.getAttachments().stream().map(BaseEntity::getId).collect(Collectors.toList()));
        }
        if (answer.getDeniedMessage() != null) {
            response.setDeniedMessage(answer.getDeniedMessage());
        }
        if (answer.getComment() != null) {
            response.setComment(answer.getComment());
        }
        response.setLiked(answer.isLiked());
        return response;
    }
}

package uz.napa.clinic.payload;

import lombok.*;
import uz.napa.clinic.entity.enums.AnswerStatus;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AnswerRequest {
    private UUID id;
    private String description;
    private AnswerStatus status;
    private List<UUID> attachmentId;
    private String deniedMessage;
    private String comment;
    private boolean liked;

}

package uz.napa.clinic.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.Application;
import uz.napa.clinic.entity.Attachment;
import uz.napa.clinic.entity.Section;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.entity.enums.ApplicationStatus;
import uz.napa.clinic.entity.template.BaseEntity;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {
    private UUID id;
    private String title;
    private String description;
    private ApplicationStatus status;
    private ApplicantResponse applicant;
    private Section section;
    private List<UUID> attachmentsId;
    private String video;
    private String audio;
    private boolean top;
    private int deadlineDay;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp deadLineDate;

    public static ApplicationResponse fromEntity(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setTitle(application.getTitle());
        response.setSection(application.getSection());
        response.setDescription(application.getDescription());
        response.setStatus(application.getStatus());
        response.setApplicant(ApplicantResponse.fromEntity(application.getCreatedBy()));
        if (application.getDeadline() != null) {
            response.setDeadlineDay((int) ((application.getDeadline().getTime() - application.getCreatedAt().getTime()) / ((1000 * 60 * 60 * 24))));
            response.setDeadLineDate(application.getDeadline());
        }
        if (!application.getAttachments().isEmpty()) {
            response.setAttachmentsId(application.getAttachments().stream().map(BaseEntity::getId).collect(Collectors.toList()));
        }
        if (application.getVideo()!=null){
            response.setVideo("/attach/video/"+application.getVideo().getId());
        }
        if (application.getAudio()!=null){
            response.setAudio("/attach/audio/"+application.getAudio().getId());
        }
        response.setTop(application.isTop());
        return response;
    }
}

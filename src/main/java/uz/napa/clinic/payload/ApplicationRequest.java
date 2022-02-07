package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRequest {
    private UUID id;
    private String title;
    private String description;
    private Long sectionId;
    private List<UUID> attachmentId;
    private UUID videoId;
    private UUID audioId;
    private Boolean top;
    private Timestamp deadline;

}

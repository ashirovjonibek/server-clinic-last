package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.DelayedApplications;
import uz.napa.clinic.entity.Section;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DelayedResponse {

    private int delayDay;

    private String comment;

    private Section section;

    private DocumentResponse documentResponse;


    public static DelayedResponse response(DelayedApplications application){
        return new DelayedResponse(
                application.getDelayDay(),
                application.getComment(),
                application.getSection(),
                DocumentResponse.fromEntity(application.getDocument())
        );
    }
}

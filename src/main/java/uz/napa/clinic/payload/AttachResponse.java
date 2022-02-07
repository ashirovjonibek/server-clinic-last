package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.Attachment;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachResponse {
    private UUID id;

    private String name;


    public static AttachResponse toFront(Attachment attachment){
        return new AttachResponse(
                attachment.getId(),
                attachment.getName()
        );
    }
}

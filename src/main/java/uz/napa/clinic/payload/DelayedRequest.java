package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.Document;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DelayedRequest {
    private UUID documentId;

    private int delayDay;

    private String comment;
}

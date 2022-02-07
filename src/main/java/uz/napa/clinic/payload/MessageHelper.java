package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.Attachment;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageHelper {

    private UUID messageId;

    private UUID fromId;

    private UUID toId;

    private String message;

    private Attachment  attachment;

    private UUID chatId;
}

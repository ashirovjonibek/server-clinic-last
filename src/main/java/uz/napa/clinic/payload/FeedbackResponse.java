package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.Document;
import uz.napa.clinic.entity.Section;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {

    private String comment;

    private boolean liked;

    private String applicantName;

    private String listenerName;

    private Section section;


    public static FeedbackResponse response(Document document){
        return new FeedbackResponse(
                document.getAnswer().getComment(),
                document.getAnswer().isLiked(),
                document.getApplication().getCreatedBy().getFullName(),
                document.getCheckedBy().getFullName(),
                document.getSection()
        );
    }

}

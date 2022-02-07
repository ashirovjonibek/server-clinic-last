package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.napa.clinic.entity.Section;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SectionResponse {
    private Long id;
    private HashMap<String, String> title;
    private HashMap<String, String> description;

    public static SectionResponse fromEntity(Section section) {
        SectionResponse response = new SectionResponse();
        response.setId(section.getId());
        response.setTitle(section.getTitle());
        if (section.getDescription() != null) {
            response.setDescription(section.getDescription());
        }
        return response;
    }
}

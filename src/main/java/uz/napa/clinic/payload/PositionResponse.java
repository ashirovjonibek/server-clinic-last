package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.Position;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionResponse {
    private Long id;
    private HashMap<String, String> title;
    private HashMap<String, String> description;

    public static PositionResponse fromEntity(Position position) {
        PositionResponse response = new PositionResponse();
        response.setId(position.getId());
        if (position.getDescription() != null) {
            response.setTitle(position.getTitle());
        }
        return response;
    }
}

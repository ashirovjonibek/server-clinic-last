package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.Menu;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuResponse {
    private Long id;
    private HashMap<String, String> title;
    private Long parentId;

    public static MenuResponse fromEntity(Menu menu) {
        MenuResponse response = new MenuResponse();
        response.setId(menu.getId());
        response.setTitle(menu.getTitle());
        if (menu.getParentMenu() != null) {
            response.setParentId(menu.getParentMenu().getId());
        }
        return response;
    }
}

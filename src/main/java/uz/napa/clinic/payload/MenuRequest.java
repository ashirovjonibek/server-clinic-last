package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuRequest {
    private Long id;
    private HashMap<String, String> title;
    private Long parentId;
}

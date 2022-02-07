package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordRequest {
    private Long id;

    private String nameuz;

    private String nameuzCyr;

    private String nameru;

    private String nameen;

    private String urluz;

    private String urluzCyr;

    private String urlru;

    private String urlen;
}

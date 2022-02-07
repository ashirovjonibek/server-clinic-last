package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponse {
    private String tokenType = "Tusiq";
    private String tokenBody;


    public JwtResponse(String tokenBody) {
        this.tokenBody = tokenBody;
    }
}

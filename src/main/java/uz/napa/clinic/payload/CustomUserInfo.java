package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.napa.clinic.entity.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserInfo {
    private User user;
    private int count;
    private String status;
}

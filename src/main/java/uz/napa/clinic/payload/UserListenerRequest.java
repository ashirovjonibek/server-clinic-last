package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListenerRequest {
    private String fullName;
    private String email;
    private Integer positionId;
    private Long districtId;
    private String phoneNumber;
    private String address;
    private Long sectionId;
    private Boolean status;
}

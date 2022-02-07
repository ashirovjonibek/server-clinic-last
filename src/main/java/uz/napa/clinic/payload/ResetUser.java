package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetUser {
    private String fullName;
    private String email;
    private Long districtId;
    private UUID imageId;
    private String phoneNumber;
    private String address;
    private Date birthDate;
    private String gender;
    private String password;
    private Long socialStatusId;
}

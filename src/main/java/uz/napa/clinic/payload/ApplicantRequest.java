package uz.napa.clinic.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ApplicantRequest {
    private UUID id;
    private String fullName;
    private Long nationId;
    private String gender;
    private Date birthDate;
    private Long districtId;
    private String address;
    private String email;
    private String phoneNumber;
    private Long socialStatusId;
    private String password;
}

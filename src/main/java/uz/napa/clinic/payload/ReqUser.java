package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUser {
    private UUID id;
    private String fullName;
    private String email;
    private Long positionId;
    private Long districtId;
    private String phoneNumber;
    private String address;
    private Date birthDate;
    private Long nationId;
    private String gender;
    private String password;
    private Long socialStatusId;
    private Boolean status;
    private Integer course;
    private Long sectionId;
}

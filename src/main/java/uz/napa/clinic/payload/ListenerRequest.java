package uz.napa.clinic.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ListenerRequest {
    private UUID id;
    private String fullName;
    private Long positionId;
    private Integer course;
    private Long sectionId;
    private String phoneNumber;
    private String email;
    private Long districtId;
    private String password;
    private Date birthDate;
    private String address;
    private String gender;
}

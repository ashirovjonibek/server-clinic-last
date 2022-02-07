package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.*;

import java.util.Date;
import java.util.UUID;

//Applicantlarni listini olish uchun
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApplicantResponse {
    private UUID id;
    private String fullName;
    private Nation nation;
    private String gender;
    private Date birthDate;
    private Long districtId;
    private String address;
    private String phoneNumber;
    private String image;
    private String email;
    private SocialStatus socialStatus;

    public static ApplicantResponse fromEntity(User user) {
        ApplicantResponse response = new ApplicantResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setNation(user.getNation());
        if (user.getAttachment()!=null){
            response.setImage("/attach/"+user.getAttachment().getId());
        }
        response.setGender(user.getGender());
        response.setBirthDate(user.getBirthDate());
        response.setDistrictId(user.getDistrict().getId());
        response.setAddress(user.getAddress());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setSocialStatus(user.getSocialStatus());
        return response;
    }
}

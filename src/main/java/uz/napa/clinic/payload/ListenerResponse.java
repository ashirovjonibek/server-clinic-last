package uz.napa.clinic.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null bo'lgan fieldlarni clientga obormaydi
public class ListenerResponse {
    private UUID id;
    private String fullName;
    private Position position;
    private Integer course;
    private String phoneNumber;
    private String email;
    private Long districtId;
    private Date birthDate;
    private String address;
    private Section section;
    private String image;
    private List<Role> roles;
    private boolean viewed;
    private boolean blocked;
    private int stars;

    public static ListenerResponse fromEntity(User user) {
        ListenerResponse response = new ListenerResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setBirthDate(user.getBirthDate());
        response.setPosition(user.getPosition());
        if (user.getCourse() != null) {
            response.setCourse(user.getCourse());
        }
        if (user.getAttachment()!=null){
            response.setImage("/attach/"+user.getAttachment().getId());
        }else response.setImage(null);
        response.setBlocked(user.isBlocked());
        response.setDistrictId(user.getDistrict().getId());
        response.setAddress(user.getAddress());
        response.setSection(user.getSection());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles());
        response.setViewed(user.isViewed());
        return response;
    }
}

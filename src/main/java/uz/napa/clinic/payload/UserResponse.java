package uz.napa.clinic.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import uz.napa.clinic.entity.Attachment;
import uz.napa.clinic.entity.Role;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.security.CustomUserDetails;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String image;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Long districtId;
    private Long sectionId;
    private Long socialStatusId;
    private Date createdAt;
    private Date updatedAt;
    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;
    private String gender;
    private boolean enabled;
    private List<Role> roles;
    private String username;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean deleted;

    public static UserResponse fromEntity(CustomUserDetails user) {
        UserResponse response = new UserResponse();
        response.setId(user.getUser().getId());
        response.setFullName(user.getUser().getFullName());
        response.setEmail(user.getUser().getEmail());
        response.setPhoneNumber(user.getUser().getPhoneNumber());
        response.setDistrictId(user.getUser().getDistrict().getId());
        if (user.getUser().getSection() != null) {
            response.setSectionId(user.getUser().getSection().getId());
        }
        if (user.getUser().getAttachment() != null) {
            response.setImage("/attach/" + user.getUser().getAttachment().getId());
        }
        response.setCreatedAt(user.getUser().getCreatedAt());
        response.setUpdatedAt(user.getUser().getUpdatedAt());
        response.setAddress(user.getUser().getAddress());
        response.setBirthDate(user.getUser().getBirthDate());
        response.setGender(user.getUser().getGender());
        response.setEnabled(user.isEnabled());
        response.setRoles(user.getUser().getRoles());
        response.setUsername(user.getUsername());
        if (user.getUser().getSocialStatus()!=null) {
            response.setSocialStatusId(user.getUser().getSocialStatus().getId());
        }
        response.setAccountNonExpired(user.isAccountNonExpired());
        response.setAccountNonLocked(user.isAccountNonLocked());
        response.setAccountNonExpired(user.isAccountNonExpired());
        return response;
    }
}

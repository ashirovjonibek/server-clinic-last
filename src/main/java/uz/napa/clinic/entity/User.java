package uz.napa.clinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.entity.template.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Column(name = "full_name")
    private String fullName;

    @NotNull
    @NotBlank(message="Please enter your email")
    @Column(name = "email",unique = true)
    private String email;

    @NotNull
    @NotBlank(message="Please enter your phone number")
    @Column(name = "phone_number",unique = true)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private District district;

    @Column(name = "address")
    private String address;

    @Column(name = "birth_date")
    private Date birthDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Nation nation;

    @Column(name = "gender")
    private String gender;

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @Column(name = "course")
    private Integer course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Section section;

    @OneToOne
    private Attachment attachment;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    @OrderBy
    @JsonIgnore
    private List<Role> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private SocialStatus socialStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Position position;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean confirmed;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean blocked;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean viewed;

    private boolean checkPhone;

    public User(String fullName, Date birthDate, Position position, int course, District district, String address, Section section, String phoneNumber, String email, String gender, UserStatus status, String password, List<Role> roles) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.position = position;
        this.course = course;
        this.district = district;
        this.address = address;
        this.section = section;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.status = status;
        this.password = password;
        this.roles = roles;
    }

}

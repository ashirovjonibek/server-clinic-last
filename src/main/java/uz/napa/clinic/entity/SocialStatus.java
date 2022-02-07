package uz.napa.clinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "social_status")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class SocialStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Type(type = "jsonb")
    @Column(name = "name", columnDefinition = "jsonb")
    private HashMap<String, String> name;
    @Type(type = "jsonb")
    @Column(name = "description", columnDefinition = "jsonb")
    private HashMap<String, String> description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "socialStatus")
    @JsonIgnore
    private List<User> users;


}

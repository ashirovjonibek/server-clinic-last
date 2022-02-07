package uz.napa.clinic.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "position")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Type(type = "jsonb")
    @Column(name = "title", columnDefinition = "jsonb")
    private HashMap<String, String> title;
    @Type(type = "jsonb")
    @Column(name = "description", columnDefinition = "jsonb")
    private HashMap<String, String> description;
}

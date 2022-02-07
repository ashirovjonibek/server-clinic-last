package uz.napa.clinic.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "words")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Words {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Lang name;

    @OneToOne
    private Lang url;

    Timestamp createdAt=new Timestamp(new Date().getTime());

    public Words(Lang name,Lang url) {
        this.name = name;
        this.url=url;
    }
}

package uz.napa.clinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import uz.napa.clinic.entity.enums.AnswerStatus;
import uz.napa.clinic.entity.template.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "answer")
public class Answer extends BaseEntity {
    @Column(name = "description",columnDefinition = "text")
    private String description;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Attachment> attachments;

    @Column(name = "denied_message",columnDefinition = "text")
    private String deniedMessage;

    @Enumerated(EnumType.STRING)
    private AnswerStatus status;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean liked;

    @Column(name = "comment",columnDefinition = "text")
    private String comment;


}

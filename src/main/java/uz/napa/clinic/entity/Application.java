package uz.napa.clinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import uz.napa.clinic.entity.enums.ApplicationStatus;
import uz.napa.clinic.entity.template.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "application")
public class Application extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Section section;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Attachment> attachments;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Attachment video;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Attachment audio;

    @Column(name = "top")
    private boolean top = false;

    private Timestamp deadline;

}

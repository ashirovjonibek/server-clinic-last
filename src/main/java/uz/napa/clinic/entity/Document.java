package uz.napa.clinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.napa.clinic.entity.enums.DocumentStatus;
import uz.napa.clinic.entity.template.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Document extends BaseEntity {
    @OneToOne(fetch = FetchType.EAGER)
    private Application application;

    @OneToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Answer answer;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_by")
    @JsonIgnore
    private User checkedBy;

    @Column(name = "comment")
    private String comment;

    @Column(columnDefinition = "text")
    private String forwardMessage;

    @ManyToOne
    Section section;
}

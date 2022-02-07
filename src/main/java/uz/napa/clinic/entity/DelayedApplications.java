package uz.napa.clinic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.template.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DelayedApplications extends BaseEntity {

    @OneToOne
    private Document document;

    private Integer delayDay;

    private String comment;

    @ManyToOne
    private Section section;
}

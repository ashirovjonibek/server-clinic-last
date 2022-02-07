package uz.napa.clinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import uz.napa.clinic.entity.template.BaseEntity;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attachment_content")
public class AttachmentContent extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private Attachment attachment;

    @Column(name = "bytes")
    private byte[] bytes;
}

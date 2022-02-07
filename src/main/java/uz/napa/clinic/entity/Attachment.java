package uz.napa.clinic.entity;

import lombok.*;
import uz.napa.clinic.entity.enums.AttachStatus;
import uz.napa.clinic.entity.template.BaseEntity;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attachment")
public class Attachment extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size")
    private Long size;

    private String fileExtension;

    private String uploadPath;

    @Enumerated(EnumType.STRING)
    private AttachStatus status;
}

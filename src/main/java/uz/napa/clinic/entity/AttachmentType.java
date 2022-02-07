package uz.napa.clinic.entity;

import lombok.*;
import uz.napa.clinic.entity.template.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attachment_type")
public class AttachmentType extends BaseEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "content_type")
    private String contentType;
    @Column(name = "width")
    private int width;
    @Column(name = "height")
    private int height;
    @Column(name = "size")
    private long size;
}

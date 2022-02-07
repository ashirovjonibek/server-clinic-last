package uz.napa.clinic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.template.BaseEntity;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MessageCenter extends BaseEntity {

    @ManyToOne
    private User to;

    @ManyToOne
    private User from;

    @Column(columnDefinition = "text")
    private String message;

    @OneToOne
    private Attachment attachment;

    private boolean edit=false;

    private boolean read=false;

    @ManyToOne
    private Chat chat;
}

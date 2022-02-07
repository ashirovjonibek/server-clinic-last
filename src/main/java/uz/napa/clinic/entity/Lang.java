package uz.napa.clinic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Lang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "text")
    private String uz;

     @Column(columnDefinition = "text")
    private String uzCyr;

    @Column(columnDefinition = "text")
    private String ru;

    @Column(columnDefinition = "text")
    private String en;

    public Lang(String uz,String uzCyr, String ru, String en) {
        this.uz = uz;
        this.uzCyr=uzCyr;
        this.ru = ru;
        this.en = en;
    }
}

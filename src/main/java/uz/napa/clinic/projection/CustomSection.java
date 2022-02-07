package uz.napa.clinic.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.napa.clinic.entity.Section;

import java.util.HashMap;

@Projection(name = "customSection", types = Section.class)
public interface CustomSection {

    Long getId();

    HashMap<String, String> getTitle();

    HashMap<String, String> getDescription();


}

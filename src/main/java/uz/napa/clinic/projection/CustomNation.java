package uz.napa.clinic.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.napa.clinic.entity.Nation;

import java.util.HashMap;

@Projection(name = "customNation", types = Nation.class)
public interface CustomNation {
    Long getId();

    HashMap<String, String> getName();

}

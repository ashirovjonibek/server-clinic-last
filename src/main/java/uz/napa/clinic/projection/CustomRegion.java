package uz.napa.clinic.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.napa.clinic.entity.Region;

import java.util.HashMap;

@Projection(name = "customRegion", types = Region.class)
public interface CustomRegion {
    Long getId();

    HashMap<String, String> getName();

}

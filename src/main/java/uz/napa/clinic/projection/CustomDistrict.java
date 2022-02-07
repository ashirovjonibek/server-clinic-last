package uz.napa.clinic.projection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import uz.napa.clinic.entity.District;

import java.util.HashMap;

@Projection(name = "customDistrict", types = District.class)
public interface CustomDistrict {
    Long getId();

    HashMap<String, String> getName();

    @Value("#{target.region?.id}")
    Long getRegionId();
}

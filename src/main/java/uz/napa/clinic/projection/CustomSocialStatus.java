package uz.napa.clinic.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.napa.clinic.entity.SocialStatus;

import java.util.HashMap;

@Projection(name = "customSocialStatus", types = SocialStatus.class)
public interface CustomSocialStatus {
    Long getId();

    HashMap<String, String> getName();

    HashMap<String, String> getDescription();


}

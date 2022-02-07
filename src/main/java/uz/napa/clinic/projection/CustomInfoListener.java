package uz.napa.clinic.projection;

import org.hibernate.annotations.Type;
import uz.napa.clinic.entity.User;

import javax.persistence.Column;
import java.util.UUID;

public interface CustomInfoListener {
    String getNumber();

    int getCount();

    String getStatus();
}

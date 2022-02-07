package uz.napa.clinic.projection;

import uz.napa.clinic.entity.User;

public interface CustomInfoCount {
    User getUser();

    int getCount();

    String getStatus();
}

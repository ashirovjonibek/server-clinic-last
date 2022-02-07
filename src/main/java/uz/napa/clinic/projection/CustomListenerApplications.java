package uz.napa.clinic.projection;

import java.sql.Timestamp;
import java.util.UUID;

public interface CustomListenerApplications {
    UUID getId();

    Timestamp getCreatedAt();

    UUID getCreatedBy();

    Timestamp getUpdatedAt();

    UUID getUpdatedBy();

    String getDescription();

    Boolean getStatus();

    String getTitle();

    Boolean getTop();

}

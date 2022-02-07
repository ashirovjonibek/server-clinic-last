package uz.napa.clinic.payload;

import uz.napa.clinic.entity.User;

import java.util.List;
import java.util.UUID;

public class ResApplication {
    private UUID id;
    private String title;
    private String description;
    private Boolean status;
    private User user;
    private Long sectionId;
    private List<UUID> attachmentsId;
    private Boolean top;
}

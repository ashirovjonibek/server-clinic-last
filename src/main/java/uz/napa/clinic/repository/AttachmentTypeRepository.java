package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.napa.clinic.entity.AttachmentType;

import java.util.UUID;

@Repository
public interface AttachmentTypeRepository extends JpaRepository<AttachmentType, UUID> {
    AttachmentType findByName(String s);
}

package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import uz.napa.clinic.entity.SocialStatus;
import uz.napa.clinic.projection.CustomSocialStatus;
public interface SocialStatusRepository extends JpaRepository<SocialStatus, Long> {
}

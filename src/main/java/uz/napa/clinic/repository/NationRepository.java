package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uz.napa.clinic.entity.Nation;
import uz.napa.clinic.projection.CustomNation;

@RepositoryRestResource(path = "nation",excerptProjection = CustomNation.class)
public interface NationRepository extends JpaRepository<Nation, Long> {
}

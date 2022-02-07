package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uz.napa.clinic.entity.Section;
import uz.napa.clinic.projection.CustomSection;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
}

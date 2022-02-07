package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.napa.clinic.entity.Position;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
}

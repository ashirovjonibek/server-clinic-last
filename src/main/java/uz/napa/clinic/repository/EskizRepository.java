package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.napa.clinic.entity.EskizToken;

public interface EskizRepository extends JpaRepository<EskizToken,Integer> {
}

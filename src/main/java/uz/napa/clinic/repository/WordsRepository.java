package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.napa.clinic.entity.Words;
@Repository
public interface WordsRepository extends JpaRepository<Words,Long> {
}

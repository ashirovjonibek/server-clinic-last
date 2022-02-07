package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.napa.clinic.entity.Lang;

public interface LangRepository extends JpaRepository<Lang,Integer> {
}

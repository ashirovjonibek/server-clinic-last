package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.napa.clinic.entity.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
}

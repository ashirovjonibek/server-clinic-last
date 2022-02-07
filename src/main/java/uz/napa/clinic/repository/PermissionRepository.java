package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.napa.clinic.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}

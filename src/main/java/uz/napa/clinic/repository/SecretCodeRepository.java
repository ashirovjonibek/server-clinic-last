package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.napa.clinic.entity.SecretCode;

public interface SecretCodeRepository extends JpaRepository<SecretCode,Long> {
    SecretCode findByPhoneNumber(String phone);
}

package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.napa.clinic.entity.PasswordResetToken;
import uz.napa.clinic.payload.PasswordRequest;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}

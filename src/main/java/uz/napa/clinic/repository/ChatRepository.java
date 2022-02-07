package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.napa.clinic.entity.Chat;

import java.util.List;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    boolean existsByCreatorIdAndReceiverId(UUID creatorId,UUID receiverId);

    Chat findByCreatorIdAndReceiverId(UUID creatorId,UUID receiverId);

    List<Chat> findByCreatorId(UUID creatorId);

    List<Chat> findAllByReceiverId(UUID receiverId);
}

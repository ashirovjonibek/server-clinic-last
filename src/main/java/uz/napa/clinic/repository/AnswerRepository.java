package uz.napa.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.napa.clinic.entity.Answer;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.entity.enums.AnswerStatus;
import uz.napa.clinic.payload.AnswerRequest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    List<Answer> findByCreatedBy(UUID id);

    //    @Query(nativeQuery = true, value = "select * from users where id=(select  user_id from answer group by user_id order by user_id desc limit 1)")
//    List<UserRes> findUser();

    List<Answer> findByStatusAndDeletedFalse(AnswerStatus status);

    List<Answer> findByStatusInAndDeletedFalse(List<AnswerStatus> status);

    List<Answer> findByCreatedByAndStatusAndDeletedFalse(User user, AnswerStatus status);

    @Query(value = "select Count(liked) from Answer \n" +
            "where createdBy=:user\n" +
            "and status='COMPLETED' and liked=false ")
    int getDislikeCount(User user);
}

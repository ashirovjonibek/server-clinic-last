package uz.napa.clinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.napa.clinic.entity.Section;
import uz.napa.clinic.entity.enums.DocumentStatus;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.payload.ListenerResponse;
import uz.napa.clinic.projection.ListenerRating;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    Optional<User> findByPhoneNumber(String number);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndDeletedFalse(UUID id);


    @Query(nativeQuery = true, value = "select * from users where id=(SELECT u.id FROM users u\n" +
            "LEFT JOIN document d \n" +
            "ON u.id = d.checked_by \n" +
            "WHERE d.checked_by IS NULL\n" +
            "and u.status='LISTENER' and u.section_id=:id limit 1) and deleted=false and blocked=false")
    User findFreeUser(Long id);

    @Query(nativeQuery = true, value = "select * from users where deleted=false and blocked=false and\n" +
            "id=(select d.checked_by from users u\n" +
            "left join document d\n" +
            "on u.id=d.checked_by\n" +
            "where u.section_id=:id and d.checked_by is not null\n" +
            "group by(checked_by) order by COUNT(checked_by) asc limit 1) ")
    User findUserBySectionId(Long id);


    List<User> findByStatusAndSectionAndDeletedFalse(UserStatus status, Section section);

    Page<User> findByStatusAndDeletedFalse(UserStatus status, Pageable pageable);

    User findByStatusAndSection(UserStatus status, Section section);

    Page<User> findByStatusAndDeletedFalseAndViewedTrue(UserStatus status,Pageable pageable);

    List<User> findByStatusAndDeletedFalseAndViewedFalse(UserStatus status);


    @Query(nativeQuery = true, value = "select u.phone_number as number,COUNT(a.liked) from users u\n" +
            "left join answer a\n" +
            "on u.id=a.created_by\n" +
            "where a.status='COMPLETED'\n" +
            "group by(u.phone_number)")
    List<ListenerRating> getListenerRating();


}

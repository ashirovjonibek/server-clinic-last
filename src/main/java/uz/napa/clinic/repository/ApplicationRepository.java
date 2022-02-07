package uz.napa.clinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.napa.clinic.entity.Application;
import uz.napa.clinic.entity.Document;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.entity.enums.ApplicationStatus;
import uz.napa.clinic.projection.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    List<Application> findAllByStatusAndDeletedFalse(ApplicationStatus status);

    Page<Application> findAllByCreatedByAndDeletedFalseOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<Application> findAllByCreatedByAndTitleContainingIgnoreCaseAndDeletedFalseOrderByCreatedAtDesc(User user,String search, Pageable pageable);

    Page<Application> findAllByCreatedByAndSectionIdAndTitleContainingIgnoreCaseAndDeletedFalseOrderByCreatedAtDesc(User user,Long sectionId,String search, Pageable pageable);

    Page<Application> findAllByCreatedByAndStatusAndTitleContainingIgnoreCaseAndDeletedFalseOrderByCreatedAtDesc(User user,ApplicationStatus status, String search, Pageable pageable);

    Page<Application> findAllByCreatedByAndStatusAndSectionIdAndTitleContainingIgnoreCaseAndDeletedFalseOrderByCreatedAtDesc(User user,ApplicationStatus status, Long sectionId, String search, Pageable pageable);

    Page<Application> findAllByCreatedByAndStatusAndDeletedFalseOrderByCreatedAtDesc(User user, ApplicationStatus status, Pageable pageable);

    Page<Application> findAllByCreatedByAndStatusAndSectionIdAndDeletedFalseOrderByCreatedAtDesc(User user, ApplicationStatus status, Long id, Pageable pageable);

    Page<Application> findAllByCreatedByAndSectionIdAndDeletedFalseOrderByCreatedAtDesc(User user,Long id, Pageable pageable);

    List<Application> findByCreatedByAndDeletedFalse(User user);

    List<Application> findTop2ByTopIsTrueAndDeletedFalseOrderByCreatedAt();

    @Query(nativeQuery = true, value = "SELECT * FROM application where top is false ORDER BY RANDOM() LIMIT 6")
    List<Application> getTopByRandom();

    List<Application> findAllByTopFalse();

    Page<Application> findAllBySectionIdAndStatusIsNotAndDeadlineBetweenOrderByCreatedAtDesc(Long id,ApplicationStatus status, Timestamp start,Timestamp end, Pageable pageable);

//    @Query(
//            value = "select * from application where id=(select application_id from answer where listener_id=:uuid) order by created_at desc",
//            countQuery = "select count(*) from application where id=(select application_id from answer where listener_id=:uuid) order by created_at desc",
//            nativeQuery = true)
//    Page<Application> findAllByAnswer(UUID uuid, Pageable pageable);

//    @Query("SELECT a FROM Application a LEFT JOIN Answer ans ON a = ans.application \n" +
//            "where ans.listener=:user")
//    Page<Application> findAllByApplicationByListener(User user, Pageable pageable);


//    @Query(nativeQuery = true, value = "select COUNT(a) from application a\n" +
//            "left join users u\n" +
//            "on a.created_by=u.id\n" +
//            "where \n" +
//            "(SELECT DATE_PART('year', now()::date) - DATE_PART('year', u.birth_date::date))>0 and \n" +
//            "(SELECT DATE_PART('year', now()::date) - DATE_PART('year', u.birth_date::date))<18")
//    CustomAge getCount(int fromDate, int toDate);

    @Query(nativeQuery = true, value = "select d.region_id as regionId,COUNT(a) from application a\n" +
            "left join users u\n" +
            "on a.created_by=u.id\n" +
            "left join district d\n" +
            "on u.district_id=d.id\n" +
            "where \n" +
            "(SELECT date_part('year', age(u.birth_date)))>:fromDate\n" +
            "and (SELECT date_part('year', age(u.birth_date)))<:toDate group by(d.region_id)")
    List<ICustomAge> getCount(int fromDate, int toDate);

    @Query(nativeQuery = true, value = "select r.id as regionId,u.gender,COUNT(u.gender) from application a\n" +
            "left join users u\n" +
            "on a.created_by=u.id\n" +
            "left join district d\n" +
            "on u.district_id=d.id\n" +
            "left join region r\n" +
            "on d.region_id=r.id\n" +
            "group by (r.id,u.gender)")
    List<CustomGender> getByGender();

    @Query(nativeQuery = true, value = "select r.id as regionId,COUNT(r.id) from application a\n" +
            "left join users u\n" +
            "on a.created_by=u.id\n" +
            "left join district d\n" +
            "on u.district_id=d.id\n" +
            "left join region r\n" +
            "on d.region_id=r.id \n" +
            "where (SELECT DATE_PART('day',a.deadline - a.created_at))>15\n" +
            "group by r.id")
    List<CustomInfoRegion> getByDenied();

    @Query(nativeQuery = true, value = "select r.id as regionId,a.status,COUNT(a.status) from application a\n" +
            "left join users u\n" +
            "on a.created_by=u.id\n" +
            "left join district d\n" +
            "on u.district_id=d.id\n" +
            "left join region r\n" +
            "on d.region_id=r.id \n" +
            "where a.status='CREATED' or a.status='INPROCESS' or a.status='COMPLETED'\n" +
            "group by (r.id,a.status)\n")
    List<CustomInfoStatus> getByStatus();

    @Query(nativeQuery = true, value = "select r.id as regionId,a.section_id as sectionId,COUNT(a.id) from application a\n" +
            "left join users u\n" +
            "on a.created_by=u.id\n" +
            "left join district d\n" +
            "on u.district_id=d.id\n" +
            "left join region r\n" +
            "on d.region_id=r.id \n" +
            "group by (r.id,a.section_id)")
    List<CustomInfoSection> getBySection();

    @Query(nativeQuery = true, value = "select r.id as regionId,u.social_status_id as socialStatusId,COUNT(u.social_status_id) from application a\n" +
            "left join users u\n" +
            "on a.created_by=u.id\n" +
            "left join district d\n" +
            "on u.district_id=d.id\n" +
            "left join region r\n" +
            "on d.region_id=r.id \n" +
            "where u.social_status_id is not null\n" +
            "group by (r.id,u.social_status_id)")
    List<CustomInfoSocialStatus> getBySocialStatus();

    @Query(nativeQuery = true, value = "select r.id as region_id,COUNT(EXTRACT(YEAR FROM a.created_at)),EXTRACT(YEAR FROM a.created_at)\n" +
            "from application a\n" +
            "left join users u\n" +
            "on a.created_by=u.id\n" +
            "left join district d\n" +
            "on u.district_id=d.id\n" +
            "left join region r\n" +
            "on d.region_id=r.id \n" +
            "where u.social_status_id is not null\n" +
            "group by (r.id,EXTRACT(YEAR FROM a.created_at))")
    List<CustomInfoYear> getByYear();

    @Query(nativeQuery = true, value = "select u.phone_number as number,COUNT(d.status),d.status from document d\n" +
            "left join users u\n" +
            "on d.checked_by=u.id\n" +
            "left join application a\n" +
            "on d.application_id=a.id\n" +
            "where d.checked_by is not null and d.status='CREATED' or d.status='COMPLETED'\n" +
            "group by (d.status,u.phone_number)")
    List<CustomInfoListener> getInfoListener();

    @Query(nativeQuery = true, value = "select array_to_json(array_agg(b))\n" +
            "      from (\n" +
            "        select  u.id,COUNT(d.status),d.status\n" +
            "        from document d\n" +
            "\t\tleft join users u\n" +
            "        on d.checked_by=u.id\n" +
            "        left join application a\n" +
            "        on d.application_id = a.id\n" +
            "\t\twhere d.checked_by is not null and d.status='CREATED' or d.status='COMPLETED' and d.checked_by=u.id\n" +
            "\t\tgroup by (d.status,u.id) \n" +
            "      ) b")
    List<CustomInfoListener> getTest();


    @Query(nativeQuery = true, value = "select u.phone_number as number,COUNT(a.created_by)  \n" +
            "from application a\n" +
            "left join users u\n" +
            "on a.created_by=u.id\n" +
            "group by(u.phone_number,a.created_by)\n" +
            "order by(COUNT(a.created_by)) desc")
    List<CustomInfoApplicant> getInfoApplicant();




}

package uz.napa.clinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.napa.clinic.entity.DelayedApplications;
import uz.napa.clinic.entity.Section;
import uz.napa.clinic.entity.enums.DocumentStatus;

import java.util.UUID;

public interface DelayedApplicationsRepository extends JpaRepository<DelayedApplications, UUID> {
    Page<DelayedApplications> findBySection(Section section, Pageable pageable);

    Page<DelayedApplications> findAllByDocumentStatusAndSection(DocumentStatus status,Section section, Pageable pageable);

    Page<DelayedApplications> findAllByDocumentStatus(DocumentStatus status, Pageable pageable);

    Page<DelayedApplications> findAllByDocumentApplicationTitleContainingIgnoreCase(String search,Pageable pageable);

    Page<DelayedApplications> findAllByDocumentApplicationTitleContainingIgnoreCaseAndDocumentStatus(String search, DocumentStatus status,Pageable pageable);

    @Query(value = "select * from delayed_applications da \n" +
            "join document d on d.id=da.document_id \n" +
            "join section s on s.id=da.section_id\n" +
            "join application app on app.id=d.application_id\n" +
            "where (app.title like '':search'%' or app.title like '%':search'%' orapp.title like '%':search'') and d.status=:status and s.id=:id",nativeQuery = true)
    Page<DelayedApplications> searchByStatusAndSection(String search,Long id, String status,Pageable pageable);
}

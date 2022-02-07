package uz.napa.clinic.service;

import uz.napa.clinic.entity.*;
import uz.napa.clinic.entity.enums.ApplicationStatus;
import uz.napa.clinic.entity.enums.DocumentStatus;
import uz.napa.clinic.payload.*;
import uz.napa.clinic.projection.*;
import uz.napa.clinic.repository.CustomInfoStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ApplicationService {
    ApiResponse create(ApplicationRequest request);

    ApplicationResponse getOne(UUID id);

    ApiResponse update(UUID id, ApplicationRequest request);

    ApiResponse delete(UUID id);

    List<ApplicationResponse> list();

    ResPageable getMyApplications(int page, int size, User user);

    ResPageable getMyApplications(int page, int size, User user, Long sectionId);

    ResPageable getMyApplications(int page, int size, User user, ApplicationStatus status);

    ResPageable getMyApplications(int page, int size, User user, String search);

    ResPageable getMyApplications(int page, int size, User user, Long sectionId, ApplicationStatus status);

    ResPageable getMyApplications(int page, int size, User user, ApplicationStatus status, String search);

    ResPageable getMyApplications(int page, int size, User user, String search, Long sectionId);

    ResPageable getMyApplications(int page, int size, User user, String search, Long sectionId, ApplicationStatus status);

    ResPageable getAllUnCheckedByListener(int page, int size, User user);

    ResPageable getAllUnCheckedByListener(int page, int size, User user, String search);

    ResPageable getAllCheckedByListener(int page, int size, User user);

    ApiResponse acceptedApplicationByListener(UUID id, User user);

    ApiResponse ignoredApplicationByListener(UUID id, Commit message, User user);

    List<ApplicationResponse> topList();

    ResPageable listByListener(User user, int page, int size);

    List<CustomInfoRegion> getByRegion();

    List<CustomInfoRegion> getByDenied();

    List<CustomAge> getByAge();

    List<CustomGender> getByGender();

    ApplicationStatistic getStatistic(User user);

    List<CustomInfoStatus> getByStatus();

    List<SectionStatusCount> getBySection();

    List<CustomInfoSocialStatus> getBySocialStatus();

    List<CustomInfoYear> getByYear();

    List<ListenerStatusCount> getInfoListener();

    List<CustomUserInfo> getInfoApplicant();

    ResPageable getDeadlineApp(Section section, int size, int page);

    ApiResponse setDeadLine(DelayedRequest request);

    ResPageable getDelayedApp(User user, int page, int siz, DocumentStatus status, String search, String filterDate);

    DelayedApplications getOneDelayedApp(UUID id);

    ResPageable searchApplicationForListener(User user, String search, int page, int size);
}

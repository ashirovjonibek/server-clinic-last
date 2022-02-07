package uz.napa.clinic.service.iml;

import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.napa.clinic.entity.*;
import uz.napa.clinic.entity.enums.ApplicationStatus;
import uz.napa.clinic.entity.enums.DocumentStatus;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.exception.BadRequestException;
import uz.napa.clinic.payload.*;
import uz.napa.clinic.projection.*;
import uz.napa.clinic.projection.ICustomAge;
import uz.napa.clinic.repository.*;
import uz.napa.clinic.service.ApplicationService;
import uz.napa.clinic.utils.CommonUtils;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final SectionServiceImpl sectionService;
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final EntityManager entityManager;
    private final RegionRepository regionRepository;
    private final SectionRepository sectionRepository;
    private final DelayedApplicationsRepository delayedApplicationsRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository, SectionServiceImpl sectionService, AttachmentRepository attachmentRepository, UserRepository userRepository, DocumentRepository documentRepository, EntityManager entityManager, RegionRepository regionRepository, SectionRepository sectionRepository, DelayedApplicationsRepository delayedApplicationsRepository) {
        this.applicationRepository = applicationRepository;
        this.sectionService = sectionService;
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.entityManager = entityManager;
        this.regionRepository = regionRepository;
        this.sectionRepository = sectionRepository;
        this.delayedApplicationsRepository = delayedApplicationsRepository;
    }

    //Ariza yaratish
    @Override
    public ApiResponse create(ApplicationRequest request) {
        try {
            Application application = new Application();
            fromRequest(application, request);
            Application savedApplication = applicationRepository.save(application);
            Document document = new Document();
            document.setStatus(DocumentStatus.CREATED);
            document.setSection(sectionRepository.findById(request.getSectionId()).orElseThrow(() -> new IllegalStateException("Section not found for create application!!!")));
            document.setApplication(savedApplication);
            User freeUser = userRepository.findFreeUser(request.getSectionId());
            if (freeUser != null) {
                document.setCheckedBy(freeUser);
            } else {
                User leastUser = userRepository.findUserBySectionId(request.getSectionId());
                if (leastUser != null) {
                    document.setCheckedBy(leastUser);
                } else {
                    User mod = userRepository.findByStatusAndSection(UserStatus.MODERATOR,sectionRepository.findById(request.getSectionId()).get());
                    if (mod != null) {
                        document.setStatus(DocumentStatus.FORWARD_TO_MODERATOR);
                    } else {
                        document.setStatus(DocumentStatus.FORWARD_TO_SUPER_MODERATOR);
                    }
                }
            }
            documentRepository.save(document);
            return new ApiResponse("Application accepted ", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Application do not created!!!", false);
        }

    }

    //Ariza o'zgartirish
    @Override
    public ApiResponse update(UUID id, ApplicationRequest request) {
        Optional<Application> findApplication = applicationRepository.findById(id);
        if (findApplication.isPresent()) {
            applicationRepository.save(fromRequest(findApplication.get(), request));
            return new ApiResponse("Application updated ", true);
        } else {
            throw new BadRequestException("Application not found with ID " + id);
        }
    }


    @Override
    public ApplicationResponse getOne(UUID id) {
        Optional<Application> findApplication = applicationRepository.findById(id);
        if (findApplication.isPresent()) {
            return ApplicationResponse.fromEntity(findApplication.get());
        } else {
            throw new BadRequestException("Application not found with ID " + id);
        }
    }

    @Override
    public ApiResponse delete(UUID id) {
        Optional<Application> findApplication = applicationRepository.findById(id);
        if (findApplication.isPresent()) {
            Application application = findApplication.get();
            application.setDeleted(true);
            applicationRepository.save(application);
            return new ApiResponse("Application deleted with ID: " + id, true);
        } else {
            throw new BadRequestException("Application not found with ID " + id);
        }
    }

    //Arizalar  royxatini olish
    @Override
    public List<ApplicationResponse> list() {
        List<Application> allApplication = applicationRepository.findAll();
        return allApplication.stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public ResPageable getMyApplications(int page, int size, User user) {
        if (user != null) {
            Pageable pageable = CommonUtils.getPageable(page, size);
            Page<Application> applicationPage = applicationRepository.findAllByCreatedByAndDeletedFalseOrderByCreatedAtDesc(user, pageable);
            return new ResPageable(
                    applicationPage.getContent().stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList()),
                    page,
                    applicationPage.getTotalPages(),
                    applicationPage.getTotalElements()
            );
        } else {
            throw new BadRequestException("User Not found ");
        }

    }

    @Override
    public ResPageable getMyApplications(int page, int size, User user, Long sectionId) {
        if (user != null) {
            Pageable pageable = CommonUtils.getPageable(page, size);
            Page<Application> applicationPage = applicationRepository.findAllByCreatedByAndSectionIdAndDeletedFalseOrderByCreatedAtDesc(user,sectionId, pageable);
            return new ResPageable(
                    applicationPage.getContent().stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList()),
                    page,
                    applicationPage.getTotalPages(),
                    applicationPage.getTotalElements()
            );
        } else {
            throw new BadRequestException("User Not found ");
        }
    }

    @Override
    public ResPageable getMyApplications(int page, int size, User user, ApplicationStatus status) {
        if (user != null) {
            Pageable pageable = CommonUtils.getPageable(page, size);
            Page<Application> applicationPage = applicationRepository.findAllByCreatedByAndStatusAndDeletedFalseOrderByCreatedAtDesc(user,status, pageable);
            return new ResPageable(
                    applicationPage.getContent().stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList()),
                    page,
                    applicationPage.getTotalPages(),
                    applicationPage.getTotalElements()
            );
        } else {
            throw new BadRequestException("User Not found ");
        }
    }

    @Override
    public ResPageable getMyApplications(int page, int size, User user, String search) {
        if (user != null) {
            Pageable pageable = CommonUtils.getPageable(page, size);
            Page<Application> applicationPage = applicationRepository.findAllByCreatedByAndTitleContainingIgnoreCaseAndDeletedFalseOrderByCreatedAtDesc(user,search, pageable);
            return new ResPageable(
                    applicationPage.getContent().stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList()),
                    page,
                    applicationPage.getTotalPages(),
                    applicationPage.getTotalElements()
            );
        } else {
            throw new BadRequestException("User Not found ");
        }
    }

    @Override
    public ResPageable getMyApplications(int page, int size, User user, Long sectionId, ApplicationStatus status) {
        if (user != null) {
            Pageable pageable = CommonUtils.getPageable(page, size);
            Page<Application> applicationPage = applicationRepository.findAllByCreatedByAndStatusAndSectionIdAndDeletedFalseOrderByCreatedAtDesc(user,status,sectionId, pageable);
            return new ResPageable(
                    applicationPage.getContent().stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList()),
                    page,
                    applicationPage.getTotalPages(),
                    applicationPage.getTotalElements()
            );
        } else {
            throw new BadRequestException("User Not found ");
        }
    }

    @Override
    public ResPageable getMyApplications(int page, int size, User user, ApplicationStatus status, String search) {
        if (user != null) {
            Pageable pageable = CommonUtils.getPageable(page, size);
            Page<Application> applicationPage = applicationRepository.findAllByCreatedByAndStatusAndTitleContainingIgnoreCaseAndDeletedFalseOrderByCreatedAtDesc(user,status,search, pageable);
            return new ResPageable(
                    applicationPage.getContent().stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList()),
                    page,
                    applicationPage.getTotalPages(),
                    applicationPage.getTotalElements()
            );
        } else {
            throw new BadRequestException("User Not found ");
        }
    }

    @Override
    public ResPageable getMyApplications(int page, int size, User user, String search, Long sectionId) {
        if (user != null) {
            Pageable pageable = CommonUtils.getPageable(page, size);
            Page<Application> applicationPage = applicationRepository.findAllByCreatedByAndSectionIdAndTitleContainingIgnoreCaseAndDeletedFalseOrderByCreatedAtDesc(user,sectionId,search, pageable);
            return new ResPageable(
                    applicationPage.getContent().stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList()),
                    page,
                    applicationPage.getTotalPages(),
                    applicationPage.getTotalElements()
            );
        } else {
            throw new BadRequestException("User Not found ");
        }
    }

    @Override
    public ResPageable getMyApplications(int page, int size, User user, String search, Long sectionId, ApplicationStatus status) {
        if (user != null) {
            Pageable pageable = CommonUtils.getPageable(page, size);
            Page<Application> applicationPage = applicationRepository.findAllByCreatedByAndStatusAndSectionIdAndTitleContainingIgnoreCaseAndDeletedFalseOrderByCreatedAtDesc(user,status,sectionId,search, pageable);
            return new ResPageable(
                    applicationPage.getContent().stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList()),
                    page,
                    applicationPage.getTotalPages(),
                    applicationPage.getTotalElements()
            );
        } else {
            throw new BadRequestException("User Not found ");
        }
    }

    @Override
    public ResPageable getAllUnCheckedByListener(int page, int size, User user) {
        Pageable pageable = CommonUtils.getPageable(page, size);
        Page<Document> uncheckedApplicationByListener = documentRepository.findByCheckedByAndStatusAndDeletedFalseAndAnswerIsNullOrderByCreatedAtDesc(user, DocumentStatus.CREATED, pageable);
        List<ApplicationResponse> applications = uncheckedApplicationByListener.stream().map(document -> ApplicationResponse.fromEntity(document.getApplication())).collect(Collectors.toList());
        return new ResPageable(
                applications,
                page,
                uncheckedApplicationByListener.getTotalPages(),
                uncheckedApplicationByListener.getTotalElements()
        );
    }

    @Override
    public ResPageable getAllUnCheckedByListener(int page, int size, User user,String search) {
        Pageable pageable = CommonUtils.getPageable(page, size);
        Page<Document> uncheckedApplicationByListener = documentRepository.findByCheckedByAndStatusAndApplicationTitleContainingIgnoreCaseAndDeletedFalseAndAnswerIsNullOrderByCreatedAtDesc(user, DocumentStatus.CREATED,search, pageable);
        List<ApplicationResponse> applications = uncheckedApplicationByListener.stream().map(document -> ApplicationResponse.fromEntity(document.getApplication())).collect(Collectors.toList());
        return new ResPageable(
                applications,
                page,
                uncheckedApplicationByListener.getTotalPages(),
                uncheckedApplicationByListener.getTotalElements()
        );
    }

    @Override
    public ResPageable getAllCheckedByListener(int page, int size, User user) {
        Pageable pageable = CommonUtils.getPageable(page, size);
        Page<Document> checkedByListener = documentRepository.findByCheckedByAndStatusAndDeletedFalseAndAnswerIsNullOrderByCreatedAtDesc(user, DocumentStatus.COMPLETED, pageable);
        List<ApplicationResponse> applications = checkedByListener.getContent().stream().map(document -> ApplicationResponse.fromEntity(document.getApplication())).collect(Collectors.toList());
        return new ResPageable(
                applications,
                page,
                checkedByListener.getTotalPages(),
                checkedByListener.getTotalElements()
        );
    }


    @Override
    public ApiResponse acceptedApplicationByListener(UUID id, User user) {
        Optional<Application> findApplication = applicationRepository.findById(id);
        if (findApplication.isPresent()) {
            Document findDocument = documentRepository.findByApplicationAndDeletedFalseAndStatus(findApplication.get(), DocumentStatus.CREATED);
            Application application = findApplication.get();
            application.setStatus(ApplicationStatus.INPROCESS);
            application.setDeadline(addDays(new Timestamp(new Date().getTime()), 30));
            findDocument.setStatus(DocumentStatus.INPROCESS);
            findDocument.setApplication(applicationRepository.save(application));
            documentRepository.save(findDocument);
            return new ApiResponse("Application accepted ", true);
        } else {
            throw new BadRequestException("Application not found with ID " + id);
        }
    }

    @Override
    public ApiResponse ignoredApplicationByListener(UUID id, Commit message, User user) {
        Optional<Application> byId = applicationRepository.findById(id);
        if (byId.isPresent()) {

            Document findDocument = documentRepository.findByApplicationAndDeletedFalseAndStatus(byId.get(), DocumentStatus.CREATED);
            if (findDocument != null) {
                if (message.getTo().equals("boss")){
                    findDocument.setStatus(DocumentStatus.FORWARD_TO_MODERATOR);
                }
                else {
                    findDocument.setStatus(DocumentStatus.FORWARD_TO_SUPER_MODERATOR);
                }
                findDocument.setForwardMessage(message.getComment());
                documentRepository.save(findDocument);
                return new ApiResponse("Application forward to Moderator", true);
            } else {
                throw new BadRequestException("Documet not found with ID " + id);
            }
        } else {
            throw new BadRequestException("Application not found with ID " + id);
        }

    }

    @Override
    public List<ApplicationResponse> topList() {
        return applicationRepository.getTopByRandom().stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public ResPageable listByListener(User user, int page, int size) {
        Pageable pageable = CommonUtils.getPageable(page, size);
        Page<Document> uncheckedDocuments = documentRepository.findByCheckedByAndStatusAndDeletedFalseAndAnswerIsNullOrderByCreatedAtDesc(user, DocumentStatus.INPROCESS, pageable);
        List<ApplicationResponse> applications = uncheckedDocuments.getContent().stream().map(document -> ApplicationResponse.fromEntity(document.getApplication())).collect(Collectors.toList());

        return new ResPageable(
                applications,
                page,
                uncheckedDocuments.getTotalPages(),
                uncheckedDocuments.getTotalElements()
        );
    }

    @Override
    public List<CustomInfoRegion> getByRegion() {
        return regionRepository.getCountByRegion();
    }

    @Override
    public List<CustomAge> getByAge() {
        List<ICustomAge> count1 = applicationRepository.getCount(0, 18);
        List<CustomAge> customAges = new ArrayList<>();

        for (ICustomAge customAge : count1) {
            CustomAge age = new CustomAge();
            CustomAgeCount customAgeCount = new CustomAgeCount();
            customAgeCount.setAge("fromZeroToSeventeen");
            customAgeCount.setCount(customAge.getCount());
            age.setRegionId(customAge.getRegionId());
            age.setCounts(Collections.singletonList(customAgeCount));
            customAges.add(age);
        }
        List<ICustomAge> count2 = applicationRepository.getCount(17, 31);
        for (ICustomAge customAge : count2) {
            CustomAge age = new CustomAge();
            CustomAgeCount customAgeCount = new CustomAgeCount();
            customAgeCount.setAge("fromEighteenToThirty");
            customAgeCount.setCount(customAge.getCount());
            age.setRegionId(customAge.getRegionId());
            age.setCounts(Collections.singletonList(customAgeCount));
            customAges.add(age);
        }
        List<ICustomAge> count3 = applicationRepository.getCount(30, 46);
        for (ICustomAge customAge : count3) {
            CustomAge age = new CustomAge();
            CustomAgeCount customAgeCount = new CustomAgeCount();
            customAgeCount.setAge("fromThirtyOneToFortyFive");
            customAgeCount.setCount(customAge.getCount());
            age.setRegionId(customAge.getRegionId());
            age.setCounts(Collections.singletonList(customAgeCount));
            customAges.add(age);
        }
        List<ICustomAge> count4 = applicationRepository.getCount(45, 60);
        for (ICustomAge customAge : count4) {
            CustomAge age = new CustomAge();
            CustomAgeCount customAgeCount = new CustomAgeCount();
            customAgeCount.setAge("fromFortySixToSixty");
            customAgeCount.setCount(customAge.getCount());
            age.setRegionId(customAge.getRegionId());
            age.setCounts(Collections.singletonList(customAgeCount));
            customAges.add(age);
        }
        List<ICustomAge> count5 = applicationRepository.getCount(60, 101);
        for (ICustomAge customAge : count5) {
            CustomAge age = new CustomAge();
            CustomAgeCount customAgeCount = new CustomAgeCount();
            customAgeCount.setAge("fromSixtyOne");
            customAgeCount.setCount(customAge.getCount());
            age.setRegionId(customAge.getRegionId());
            age.setCounts(Collections.singletonList(customAgeCount));
            customAges.add(age);
        }

        List<CustomAge> list = new ArrayList<>();

        boolean isNot = false;
        for (CustomAge customAge : customAges) {
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getRegionId().equals(customAge.getRegionId())) {
                        isNot = true;
                    }
                }
                if (!isNot) {
                    CustomAge age = new CustomAge();
                    List<CustomAgeCount> counts = new ArrayList<>();
                    for (CustomAge customAge1 : customAges) {
                        CustomAgeCount countSection = new CustomAgeCount();
                        if (customAge.getRegionId().equals(customAge1.getRegionId())) {
                            countSection = customAge1.getCounts().get(0);
                            counts.add(countSection);
                        }

                    }
                    age.setRegionId(customAge.getRegionId());
                    age.setCounts(counts);
                    list.add(age);
                }
                isNot = false;
            } else {
                CustomAge age = new CustomAge();
                List<CustomAgeCount> counts = new ArrayList<>();
                for (CustomAge customAge1 : customAges) {
                    CustomAgeCount count = new CustomAgeCount();
                    if (customAge.getRegionId().equals(customAge1.getRegionId())) {
                        count = customAge1.getCounts().get(0);
                        counts.add(count);
                    }
                }
                age.setRegionId(customAge.getRegionId());
                age.setCounts(counts);
                list.add(age);
            }
        }
        return list;

//        byAge.put("fromZeroToSeventeen", 0L);
//        byAge.put("fromEighteenToThirty", 0L);
//        byAge.put("fromThirtyOneToFortyFive", 0L);
//        byAge.put("fromFortySixToSixty", 0L);
//        byAge.put("fromSixtyOne", 0L);
    }

    @Override
    public List<CustomGender> getByGender() {
        return applicationRepository.getByGender();
    }

    @Override
    public ApplicationStatistic getStatistic(User user) {
        Map<String,Object> response=new HashMap<>();
        List<Application> all = applicationRepository.findAll();
        List<Section> all1 = sectionRepository.findAll();
        Map<String,Object> sections=new HashMap<>();
        List<DelayedApplications> all2 = delayedApplicationsRepository.findAll();
        response.put("allApplications",all.size());
        int a=0,b=0,n=0,completed=0,thisDayNew=0,thisDayComplete=0;
        Timestamp time=new Timestamp(new Date().getTime());
        for (int i = 0; i < all.size(); i++) {
            if (!all.get(i).getStatus().equals(ApplicationStatus.COMPLETED)&&!all.get(i).getStatus().equals(ApplicationStatus.CREATED)) a++;
            if ((!all.get(i).getStatus().equals(ApplicationStatus.COMPLETED)&&!all.get(i).getStatus().equals(ApplicationStatus.CREATED))&&
                all.get(i).getDeadline().getTime()<time.getTime()
            ) b++;
            if (all.get(i).getStatus().equals(ApplicationStatus.CREATED))n++;
            if (all.get(i).getStatus().equals(ApplicationStatus.COMPLETED))completed++;
            if (all.get(i).getStatus().equals(ApplicationStatus.CREATED)&&
                    (
                            all.get(i).getCreatedAt().getDay()==time.getDay()&&
                            all.get(i).getCreatedAt().getMonth()==time.getMonth()
                    )
            )thisDayNew++;
            if (all.get(i).getStatus().equals(ApplicationStatus.COMPLETED)&&
                    (
                            all.get(i).getUpdatedAt().getDay()==time.getDay()&&
                                    all.get(i).getUpdatedAt().getMonth()==time.getMonth()
                    ))thisDayComplete++;
        }
        for (int i = 0; i < all1.size(); i++) {
            int counter=0;
            int aIn=0,bIn=0,nIn=0,completedIn=0,thisDayNewIn=0,thisDayCompleteIn=0,delayedIn=0;
            for (int j = 0; j < all.size(); j++) {
                if (all1.get(i).getId().equals(all.get(j).getSection().getId()))counter++;
                if (all1.get(i).getId().equals(all.get(j).getSection().getId())&&all.get(j).getStatus().equals(ApplicationStatus.INPROCESS)&&all.get(j).getStatus().equals(ApplicationStatus.DENIED)) aIn++;
                if (all1.get(i).getId().equals(all.get(j).getSection().getId())&&(!all.get(j).getStatus().equals(ApplicationStatus.COMPLETED)&&!all.get(j).getStatus().equals(ApplicationStatus.CREATED))&&
                        all.get(j).getDeadline().getTime()<time.getTime()
                ) bIn++;
                if (all1.get(i).getId().equals(all.get(j).getSection().getId())&&all.get(j).getStatus().equals(ApplicationStatus.CREATED))nIn++;
                if (all1.get(i).getId().equals(all.get(j).getSection().getId())&&all.get(j).getStatus().equals(ApplicationStatus.COMPLETED))completedIn++;
                if (all1.get(i).getId().equals(all.get(j).getSection().getId())&&
                        (
                                all.get(j).getCreatedAt().getDay()==time.getDay()&&
                                        all.get(j).getCreatedAt().getMonth()==time.getMonth()
                        )
                )thisDayNewIn++;
                if (all1.get(i).getId().equals(all.get(j).getSection().getId())&&all.get(j).getStatus().equals(ApplicationStatus.COMPLETED)&&
                        (
                                all.get(j).getUpdatedAt().getDay()==time.getDay()&&
                                        all.get(j).getUpdatedAt().getMonth()==time.getMonth()
                        ))thisDayCompleteIn++;
            }
            for (int j = 0; j < all2.size(); j++) {
                if (all1.get(i).getId()==all2.get(j).getSection().getId())delayedIn++;
            }
            Map<String,Object> section=new HashMap<>();
            section.put("this",all1.get(i));
            section.put("inProcessApplications",aIn);
            section.put("deadlineEndEndingApplications",bIn);
            section.put("newApplications",nIn);
            section.put("completeApplications",completedIn);
            section.put("thisDayNewApplications",thisDayNewIn);
            section.put("thisDayCompleteApplications",thisDayCompleteIn);
            section.put("count",counter);
            section.put("delayDeadlineApplications",delayedIn);
            sections.put(""+all1.get(i).getId(),section);
        }
        response.put("inProcessApplications",a);
        response.put("deadlineEndEndingApplications",b);
        response.put("newApplications",n);
        response.put("completeApplications",completed);
        response.put("thisDayNewApplications",thisDayNew);
        response.put("thisDayCompleteApplications",thisDayComplete);
        response.put("delayDeadlineApplications",all2.size());
        response.put("sections",sections);

        return new ApplicationStatistic(response);
    }

    @Override
    public List<CustomInfoRegion> getByDenied() {
        return applicationRepository.getByDenied();
    }

    @Override
    public List<CustomInfoStatus> getByStatus() {
        return applicationRepository.getByStatus();
    }

    @Override
    public List<SectionStatusCount> getBySection() {
        List<CustomInfoSection> bySection = applicationRepository.getBySection();
        List<SectionStatusCount> list = new ArrayList<>();
        boolean isNot = false;
        for (CustomInfoSection customInfoSection : bySection) {
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getRegionId().equals(customInfoSection.getRegionId())) {
                        isNot = true;
                    }
                }
                if (!isNot) {
                    SectionStatusCount statusCount = new SectionStatusCount();
                    List<CustomCountSection> counts = new ArrayList<>();
                    for (CustomInfoSection infoSection : bySection) {
                        CustomCountSection countSection = new CustomCountSection();
                        if (customInfoSection.getRegionId().equals(infoSection.getRegionId())) {
                            countSection.setCount(infoSection.getCount());
                            countSection.setSectionId(infoSection.getSectionId());
                            counts.add(countSection);
                        }

                    }
                    statusCount.setRegionId(customInfoSection.getRegionId());
                    statusCount.setCounts(counts);
                    list.add(statusCount);
                }
                isNot = false;
            } else {
                SectionStatusCount statusCount = new SectionStatusCount();
                List<CustomCountSection> counts = new ArrayList<>();
                for (CustomInfoSection infoSection : bySection) {
                    CustomCountSection count = new CustomCountSection();
                    if (customInfoSection.getRegionId().equals(infoSection.getRegionId())) {
                        count.setCount(infoSection.getCount());
                        count.setSectionId(infoSection.getSectionId());
                        counts.add(count);
                    }
                }
                statusCount.setRegionId(customInfoSection.getRegionId());
                statusCount.setCounts(counts);
                list.add(statusCount);
            }
        }
        return list;
    }

    @Override
    public List<CustomInfoSocialStatus> getBySocialStatus() {
        return applicationRepository.getBySocialStatus();
    }

    @Override
    public List<CustomInfoYear> getByYear() {
        return applicationRepository.getByYear();
    }

    @Override
    public List<ListenerStatusCount> getInfoListener() {
        List<CustomInfoListener> infoListener = applicationRepository.getInfoListener();
        List<ListenerStatusCount> list = new ArrayList<>();
        for (CustomInfoListener customInfoListener : infoListener) {
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    if (!list.get(i).getListener().getPhoneNumber().equals(customInfoListener.getNumber())) {
                        ListenerStatusCount userTest = new ListenerStatusCount();
                        List<CustomCount> counts = new ArrayList<>();
                        for (CustomInfoListener listener : infoListener) {
                            CustomCount count = new CustomCount();
                            if (customInfoListener.getNumber().equals(listener.getNumber())) {
                                count.setStatus(listener.getStatus());
                                count.setCount(listener.getCount());
                                counts.add(count);
                            }

                        }
                        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(customInfoListener.getNumber());
                        if (byPhoneNumber.isPresent()) {
                            userTest.setListener(ListenerResponse.fromEntity(byPhoneNumber.get()));
                            userTest.setCounts(counts);
                        }
                        list.add(userTest);
                    }
                }
            } else {
                ListenerStatusCount userTest = new ListenerStatusCount();
                List<CustomCount> counts = new ArrayList<>();
                for (CustomInfoListener listener : infoListener) {
                    CustomCount count = new CustomCount();
                    if (customInfoListener.getNumber().equals(listener.getNumber())) {
                        count.setStatus(listener.getStatus());
                        count.setCount(listener.getCount());
                        counts.add(count);
                    }

                }
                Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(customInfoListener.getNumber());
                if (byPhoneNumber.isPresent()) {
                    userTest.setListener(ListenerResponse.fromEntity(byPhoneNumber.get()));
                    userTest.setCounts(counts);
                }
                list.add(userTest);
            }
        }
        return list;
    }

    @Override
    public List<CustomUserInfo> getInfoApplicant() {
        List<CustomInfoApplicant> infoApplicant = applicationRepository.getInfoApplicant();
        List<CustomUserInfo> userInfos = new ArrayList<>();
        for (CustomInfoApplicant customInfoApplicant : infoApplicant) {
            CustomUserInfo userInfo = new CustomUserInfo();
            Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(customInfoApplicant.getNumber());
            if (byPhoneNumber.isPresent()) {
                userInfo.setUser(byPhoneNumber.get());
                userInfo.setCount(customInfoApplicant.getCount());
            }
            userInfos.add(userInfo);
        }
        return userInfos;
    }

    @Override
    public ResPageable getDeadlineApp(Section section, int size, int page) {
        Pageable pageable = CommonUtils.getPageable(page, size);
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar.setTime(new Date());
//        calendar1.setTime(new Date());
        calendar.add(Calendar.DATE, 5);
        calendar1.setTime(removeDate(15));
        System.out.println(removeDate(15)+calendar.getTime().toString());
        Page<Application> allDeadline = applicationRepository
                .findAllBySectionIdAndStatusIsNotAndDeadlineBetweenOrderByCreatedAtDesc(
                section.getId(),
                ApplicationStatus.COMPLETED,
                new Timestamp(calendar1.getTime().getTime()),
                new Timestamp(calendar.getTime().getTime()),pageable
        );
        List<Document> documents = new ArrayList<>();
        allDeadline.getContent().forEach(application -> {
            documents.add(documentRepository.findByApplicationAndAndDeletedFalse(application));
        });
        return new ResPageable(
                documents.stream().map(application -> DocumentResponse.fromEntity(application)).collect(Collectors.toList()),
                page,
                allDeadline.getTotalPages(),
                allDeadline.getTotalElements()
        );
    }

    @Override
    public ApiResponse setDeadLine(DelayedRequest request) {
        Document document;
        if (request.getDocumentId()!=null){
             document = documentRepository.findById(request.getDocumentId()).orElseThrow(() -> new IllegalStateException("Document not found for set application deadline day!!!"));
        }else {
            return new ApiResponse("Document id is required!!!",false);
        }
        DelayedApplications delayedApplications=new DelayedApplications();
        document.getApplication().setDeadline(addDays(new Timestamp(new Date().getTime()), request.getDelayDay()));
        delayedApplications.setComment(request.getComment());
        delayedApplications.setDelayDay(request.getDelayDay());
        delayedApplications.setSection(document.getSection());
        try {
            Document save = documentRepository.save(document);
            delayedApplications.setDocument(save);
            delayedApplicationsRepository.save(delayedApplications);
            return new ApiResponse("Successfully updated!!!",true);
        }catch (Exception e){
            e.printStackTrace();
            return new ApiResponse("Error for updated!!!",false);
        }
    }

    @Override
    public ResPageable getDelayedApp(User user,int page, int size, DocumentStatus status, String search, String filterDate) {
        Pageable pageable = CommonUtils.getPageable(page, size);

        if (user.getStatus().equals(UserStatus.SUPER_MODERATOR)
            ||user.getStatus().equals(UserStatus.SUPER_MODERATOR_AND_MODERATOR)
                ||user.getStatus().equals(UserStatus.ADMIN)
        ){
            Page<DelayedApplications> all;
            if (!status.equals(DocumentStatus.ALL)){
                if (search.equals(""))
                all=delayedApplicationsRepository.findAllByDocumentStatus(status,pageable);
                else
                    all=delayedApplicationsRepository.findAllByDocumentApplicationTitleContainingIgnoreCaseAndDocumentStatus(search,status,pageable);
            }else if (!search.equals("")){
                all=delayedApplicationsRepository.findAllByDocumentApplicationTitleContainingIgnoreCase(search,pageable);
            }
            else {
                all= delayedApplicationsRepository.findAll(pageable);
            }
            return new ResPageable(
                    all.getContent().stream().map(applications -> DelayedResponse.response(applications)).collect(Collectors.toList()),
                    page,
                    all.getTotalPages(),
                    all.getTotalElements()
            );
        }else {
            Page<DelayedApplications> all;
//            if (!status.equals("")){
//                all=delayedApplicationsRepository.findAllByDocumentStatusAndSection(status,user.getSection(),pageable);
//            }else {
//            }
            all= delayedApplicationsRepository.findBySection(user.getSection(),pageable);
            return new ResPageable(
                    all.getContent().stream().map(applications -> DelayedResponse.response(applications)).collect(Collectors.toList()),
                    page,
                    all.getTotalPages(),
                    all.getTotalElements()
            );
        }

    }

    public DelayedApplications getOneDelayedApp(UUID id){
        return delayedApplicationsRepository.findById(id).orElseThrow(()->new IllegalStateException("Delayed app not found for this id!!!"));
    }

    private Application fromRequest(Application application, ApplicationRequest request) {
        application.setTitle(request.getTitle());
        application.setDescription(request.getDescription());
        application.setStatus(ApplicationStatus.CREATED);
        if (request.getAttachmentId()!=null) {
            application.setAttachments(attachmentRepository.findAllById(request.getAttachmentId()));
        }
        if (request.getVideoId() != null) {
            application.setVideo(attachmentRepository.findById(request.getVideoId())
                    .orElseThrow(() -> new IllegalStateException("Video not found for application")));
        }
        if (request.getAudioId() != null) {
            application.setAudio(attachmentRepository.findById(request.getAudioId())
                    .orElseThrow(() -> new IllegalStateException("Audio not found for application")));
        }
        application.setSection(entityManager.getReference(Section.class, request.getSectionId()));
        if (request.getTop() != null) {
            application.setTop(request.getTop());
        }
        return application;
    }

    public Timestamp addDays(Timestamp date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);// w ww.  j ava  2  s  .co m
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return new Timestamp(cal.getTime().getTime());

    }

    public Date removeDate(int rDays) {
        long dateOffset = (24*60*60*1000) * rDays;
        Date myDate = new Date();
        myDate.setTime(myDate.getTime() - dateOffset);
        return myDate;

    }


    public ResPageable searchApplicationForListener(User user,String search,int page,int size){
        Pageable pageable = CommonUtils.getPageable(page, size);
        Page<Document> uncheckedDocuments = documentRepository.findByCheckedByAndStatusAndApplicationTitleContainingIgnoreCaseAndDeletedFalseAndAnswerIsNullOrderByCreatedAtDesc(user, DocumentStatus.INPROCESS,search, pageable);
        List<ApplicationResponse> applications = uncheckedDocuments.getContent().stream().map(document -> ApplicationResponse.fromEntity(document.getApplication())).collect(Collectors.toList());

        return new ResPageable(
                applications,
                page,
                uncheckedDocuments.getTotalPages(),
                uncheckedDocuments.getTotalElements()
        );
    }

    public ApiResponse getSts() {

        int size = applicationRepository.findAll().size();
        Page<User> users = userRepository.findByStatusAndDeletedFalse(UserStatus.APPLICANT, CommonUtils.getPageable(0, 10));
        Long applicants=users.getTotalElements();
        int complete = applicationRepository.findAllByStatusAndDeletedFalse(ApplicationStatus.COMPLETED).size();
        List<ApplicationStatus> statuses=new ArrayList<>();
//        statuses.add(ApplicationStatus.INPROCESS);
//        statuses.add(ApplicationStatus.DENIED);
        int inp = applicationRepository.findAllByStatusAndDeletedFalse(ApplicationStatus.DENIED).size()+applicationRepository.findAllByStatusAndDeletedFalse(ApplicationStatus.INPROCESS).size();
        Map<String, Long> response=new HashMap<>();

        response.put("all", (long) size);
        response.put("inprocces", (long) inp);
        response.put("complete", (long) complete);
        response.put("applicants",applicants);


        return new ApiResponse("sts",true,response);
    }
}

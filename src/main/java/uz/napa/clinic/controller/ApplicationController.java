package uz.napa.clinic.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.napa.clinic.entity.Section;
import uz.napa.clinic.entity.enums.ApplicationStatus;
import uz.napa.clinic.entity.enums.DocumentStatus;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.payload.*;
import uz.napa.clinic.repository.AttachmentRepository;
import uz.napa.clinic.repository.SectionRepository;
import uz.napa.clinic.repository.UserRepository;
import uz.napa.clinic.security.CurrentUser;
import uz.napa.clinic.security.CustomUserDetails;
import uz.napa.clinic.service.iml.ApplicationServiceImpl;
import uz.napa.clinic.utils.AppConstants;
import uz.napa.clinic.utils.CommonUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/application")
public class ApplicationController {
    private static final String CREATE = "/create";
    private static final String UPDATE = "/{id}";
    private static final String GET_BY_ID = "/{id}";
    private static final String GET_UNCHEKCED_APPLICATION = "/listener";
    private static final String GET_ALL_MY_APPLICATIONS = "/applicant";
    private static final String ACCEPTED_BY_LISTENER = "/accepted";
    private static final String IGNORED_BY_LISTENER = "/ignored";
    private static final String ACCEPTED_APPLICATIONS_BY_LISTENER = "/unchecked";
    private static final String TOP_APPLICATION = "/top";
    private static final String GET_BY_REGION = "/filterByRegion";
    private static final String GET_BY_AGE = "/filterByAge";
    private static final String GET_BY_GENDER = "/filterByGender";
    private static final String GET_BY_DENIED = "/filterByDenied";
    private static final String GET_BY_STATUS = "/filterByStatus";
    private static final String GET_BY_SECTION = "/filterBySection";
    private static final String GET_BY_SOCIALSTATUS = "/filterBySocialStatus";
    private static final String GET_BY_YEAR = "/filterByYear";
    private static final String GET_BY_GIVEN_YEAR = "/filterByYear";
    private static final String INFO_APPLICANT = "/info/applicant";
    private static final String INFO_LISTENER = "/info/listener";
    private static final String DEADLINE_APPLICATIONS = "/deadline_applications";
    private static final String GET_BY_STATUS_COUNT = "/get-by-status-count";
    private static final String SET_DEADLINE_DATE = "/set-deadline";
    private static final String GET_DELAYED_APP = "/get-delayed-app";
    private static final String GET_ONE_DELAYED_APP = "/get-delayed-app/{id}";
    private static final String HOME_STATISTIC = "/home-statistic";
    final
    ApplicationServiceImpl applicationService;
    final
    UserRepository userRepository;
    final
    SectionRepository sectionRepository;
    final
    AttachmentRepository attachmentRepository;

    public ApplicationController(ApplicationServiceImpl applicationService, UserRepository userRepository, SectionRepository sectionRepository, AttachmentRepository attachmentRepository) {
        this.applicationService = applicationService;
        this.userRepository = userRepository;
        this.sectionRepository = sectionRepository;
        this.attachmentRepository = attachmentRepository;
    }

    //Ariza yaratish
    @PostMapping(CREATE)
    public HttpEntity<?> create(@RequestBody ApplicationRequest request) {
        ApiResponse apiResponse = applicationService.create(request);
        return ResponseEntity.status(apiResponse.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResponse);
    }

    //Arizani yangilash
    @PutMapping(UPDATE)
    public HttpEntity<?> updateApplication(@PathVariable UUID id, @RequestBody ApplicationRequest request) {
        ApiResponse response = applicationService.update(id, request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(SET_DEADLINE_DATE)
    public HttpEntity<?> setDeadlineDate(@RequestBody DelayedRequest request) {
        ApiResponse response = applicationService.setDeadLine(request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    // Listenerga kelib tushgan lekin tekshirilmagan arizalar
    @GetMapping(GET_UNCHEKCED_APPLICATION)
    public HttpEntity<?> getAllUnCheckedByListener(@CurrentUser CustomUserDetails user,
                                                   @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                                   @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size,
                                                   @RequestParam(name = "search", defaultValue = "") String search
    ) {
        if (search.equals("")) {
            return ResponseEntity.ok(applicationService.getAllUnCheckedByListener(page, size, user.getUser()));
        } else {
            return ResponseEntity.ok(applicationService.getAllUnCheckedByListener(page, size, user.getUser(), search));
        }
    }

    // Listenerga kelib tushgan va u javob berish uchun qabul qilganda
    @PutMapping(ACCEPTED_BY_LISTENER)
    public ResponseEntity<?> acceptedByListener(@RequestParam UUID id, @CurrentUser CustomUserDetails userDetails) {
        return ResponseEntity.ok(applicationService.acceptedApplicationByListener(id, userDetails.getUser()));
    }

    //Listenerga kelib tushgan va u javob berish uchun qabul qilgan arizalar ro'yxati
    @GetMapping(ACCEPTED_APPLICATIONS_BY_LISTENER)
    public ResponseEntity<?> getAcceptedUncheckedApplication(@CurrentUser CustomUserDetails userDetails,
                                                             @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                                             @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size,
                                                             @RequestParam(name = "search", defaultValue = "") String search

    ) {
        if (search.equals("")) {
            return ResponseEntity.ok(applicationService.listByListener(userDetails.getUser(), page, size));
        } else {
            return ResponseEntity.ok(applicationService.searchApplicationForListener(userDetails.getUser(), search, page, size));
        }

    }

    //Listenerga kelib tushgan lekin va rad etilganda(Bu bolimga tegishli bo'lmagani uchun)
    @PutMapping(IGNORED_BY_LISTENER)
    public ResponseEntity<?> ignoredByListener(@RequestParam UUID id, @RequestBody Commit message, @CurrentUser CustomUserDetails userDetails) {
        return ResponseEntity.ok(applicationService.ignoredApplicationByListener(id, message, userDetails.getUser()));
    }


    //Arizani ID bo'yicha olish
    @GetMapping(GET_BY_ID)
    public HttpEntity<?> getApplication(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationService.getOne(id));
    }

    @GetMapping(GET_BY_STATUS_COUNT)
    public HttpEntity<?> getApplicationStatistic(@CurrentUser CustomUserDetails user) {
        return ResponseEntity.ok(applicationService.getStatistic(user.getUser()));
    }

    //Arizachiga tegishli bolgan arizalar tekshirilgan va tekshirilmagan
    @GetMapping(GET_ALL_MY_APPLICATIONS)
    public HttpEntity<?> getMyApplications(@CurrentUser CustomUserDetails userDetails,
                                           @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                           @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size,
                                           @RequestParam(name = "search", defaultValue = "") String search,
                                           @RequestParam(name = "status", defaultValue = "ALL") ApplicationRequestStatus statusReq,
                                           @RequestParam(name = "sectionId", defaultValue = "0") Long sectionId
    ) {
        ApplicationStatus status;
        if (statusReq.equals(ApplicationRequestStatus.ALL))
            status = ApplicationStatus.ALL;
        else if (statusReq.equals(ApplicationRequestStatus.CREATED))
            status = ApplicationStatus.CREATED;
        else if (statusReq.equals(ApplicationRequestStatus.INPROCESS))
            status = ApplicationStatus.INPROCESS;
        else status = ApplicationStatus.COMPLETED;


        if (status.equals(ApplicationStatus.ALL) && search.equals("") && sectionId == 0) {
            return ResponseEntity.ok(new ApiResponse("You applications", true, applicationService.getMyApplications(page, size, userDetails.getUser())));
        } else if (!status.equals(ApplicationStatus.ALL) && search.equals("") && sectionId == 0) {
            return ResponseEntity.ok(new ApiResponse("You applications", true, applicationService.getMyApplications(page, size, userDetails.getUser(), status)));
        } else if (status.equals(ApplicationStatus.ALL) && !search.equals("") && sectionId == 0) {
            return ResponseEntity.ok(new ApiResponse("You applications", true, applicationService.getMyApplications(page, size, userDetails.getUser(), search)));
        } else if (status.equals(ApplicationStatus.ALL) && search.equals("") && sectionId != 0) {
            return ResponseEntity.ok(new ApiResponse("You applications", true, applicationService.getMyApplications(page, size, userDetails.getUser(), sectionId)));
        } else if (!status.equals(ApplicationStatus.ALL) && !search.equals("") && sectionId == 0) {
            return ResponseEntity.ok(new ApiResponse("You applications", true, applicationService.getMyApplications(page, size, userDetails.getUser(), status, search)));
        } else if (status.equals(ApplicationStatus.ALL) && !search.equals("") && sectionId != 0) {
            return ResponseEntity.ok(new ApiResponse("You applications", true, applicationService.getMyApplications(page, size, userDetails.getUser(), search, sectionId)));
        } else if (!status.equals(ApplicationStatus.ALL) && search.equals("") && sectionId != 0) {
            return ResponseEntity.ok(new ApiResponse("You applications", true, applicationService.getMyApplications(page, size, userDetails.getUser(), sectionId, status)));
        } else {
            return ResponseEntity.ok(new ApiResponse("You applications", true, applicationService.getMyApplications(page, size, userDetails.getUser(), search, sectionId, status)));
        }
    }


    //top applications
    @GetMapping(TOP_APPLICATION)
    public ResponseEntity<?> getTopApplication() {
        return ResponseEntity.ok(applicationService.topList());
    }

    // Ariza larni region boyicha olish statistika uchun
    @GetMapping(GET_BY_REGION)
    public ResponseEntity<?> getByRegion() {
        return ResponseEntity.ok(applicationService.getByRegion());
    }

    // Ariza larni yosh boyicha olish statistika uchun
    @GetMapping(GET_BY_AGE)
    public ResponseEntity<?> getByAge() {
        return ResponseEntity.ok(applicationService.getByAge());
    }

    // Ariza larni gender boyicha olish statistika uchun
    @GetMapping(GET_BY_GENDER)
    public ResponseEntity<?> getByGender() {
        return ResponseEntity.ok(applicationService.getByGender());
    }

    //  kechiktirlib javob berilgan arizalar statistika uchun
    @GetMapping(GET_BY_DENIED)
    public ResponseEntity<?> getByDenied() {
        return ResponseEntity.ok(applicationService.getByDenied());
    }

    // statistikani status boyicha olish
    @GetMapping(GET_BY_STATUS)
    public ResponseEntity<?> getByStatus() {
        return ResponseEntity.ok(applicationService.getByStatus());
    }

    // statistikani bo'lim boyicha olish
    @GetMapping(GET_BY_SECTION)
    public ResponseEntity<?> getBySection() {
        return ResponseEntity.ok(applicationService.getBySection());
    }

    // statistikani ijtimoiy holat boyicha olish
    @GetMapping(GET_BY_SOCIALSTATUS)
    public ResponseEntity<?> getBySocialStatus() {
        return ResponseEntity.ok(applicationService.getBySocialStatus());
    }

    // statistikani yil  boyicha olish
    @GetMapping(GET_BY_YEAR)
    public ResponseEntity<?> getByYear() {
        return ResponseEntity.ok(applicationService.getByYear());
    }

    @GetMapping(INFO_LISTENER)
    public ResponseEntity<?> getInfoListener() {
        return ResponseEntity.ok(applicationService.getInfoListener());
    }

    @GetMapping(INFO_APPLICANT)
    public ResponseEntity<?> getInfoApplicant() {
        return ResponseEntity.ok(applicationService.getInfoApplicant());
    }

    @GetMapping(DEADLINE_APPLICATIONS)
    public ResponseEntity<?> getDeadlineApp(@CurrentUser CustomUserDetails user,
                                            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size) {
        if (!user.getUser().getStatus().equals(UserStatus.APPLICANT)) {
            return ResponseEntity.ok(applicationService.getDeadlineApp(user.getUser().getSection(), size, page));
        } else
            return ResponseEntity.ok("Permission denied");
    }

    @GetMapping(GET_DELAYED_APP)
    public HttpEntity<?> getDelayedApp(
            @CurrentUser CustomUserDetails user,
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "status",defaultValue = "ALL") DocumentStatus status,
            @RequestParam(name = "filterDate", defaultValue = "") String filterDate
    ) {
        return ResponseEntity.ok(applicationService.getDelayedApp(user.getUser(), page, size, DocumentStatus.ALL, search, filterDate));
    }

    @GetMapping(GET_ONE_DELAYED_APP)
    public HttpEntity<?> getOneDelayedApp(
            @CurrentUser CustomUserDetails user,
            @PathVariable("id") UUID id
    ) {
        if (user.getUser().getStatus().equals(UserStatus.ADMIN)) {
            return ResponseEntity.ok(applicationService.getOneDelayedApp(id));
        } else {
            return ResponseEntity.ok("Permission denied");
        }

    }

    @GetMapping(HOME_STATISTIC)
    public HttpEntity<?> getSts(){
        return ResponseEntity.ok(applicationService.getSts());
    }
//
//    // statistikani yil  boyicha olish
//    @GetMapping(GET_BY_GIVEN_YEAR)
//    public ResponseEntity<?> getBySpecialYear(@RequestParam int year) {
//        return ResponseEntity.ok(applicationService.getByGiven());
//    }


}

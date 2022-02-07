package uz.napa.clinic.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.napa.clinic.entity.enums.DocumentStatus;
import uz.napa.clinic.payload.AnswerRequest;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.Commit;
import uz.napa.clinic.security.CurrentUser;
import uz.napa.clinic.security.CustomUserDetails;
import uz.napa.clinic.service.DocumentService;
import uz.napa.clinic.utils.AppConstants;
import uz.napa.clinic.utils.CommonUtils;

import java.util.UUID;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/document")
public class DocumentController {
    private static final String SET_LISTENER = "/set/listener";
    private static final String SET_SECTION = "/set/section";
    private static final String GET_DOCUMENT_BY_APPLICATION = "/application/{id}";
    private static final String GET_BOSS_DOCUMENTS = "/boss/answers";
    private static final String ACCEPT_BY_BOSS = "/boss/accept/{id}";
    private static final String DENIED_BY_BOSS = "/boss/denied/{id}";
    private static final String CHECKED_BY_LISTENER = "/listener/checked";
    private static final String GET_DENIED_DOCUMENT = "/listener/denied";
    private static final String GET_COMPLETED_APPLICATIONDOC_BY_APPLICANT = "/applicant";
    private static final String GET_LIST = "/list";
    private static final String GET_ACCEPTED_LIST = "/accepted/all";
    private static final String GET_ACCEPTED_APPLICATION_BY_SECTION = "/accepted/section";
    private static final String GET_APPLICATIONS_LISTENER_IS_NULL = "/applications";
    private static final String GET_DOCUMENT_APPLICATION_TO_SEND = "/sending";
    private static final String GET_FEEDBACK = "/answer/feedback";
    private static final String GET_BY_STATUS = "/get-by-status";
    private static final String GET_BY_CHECKED_BY_LISTENER = "/get-by-checked-listener";
    private static final String GET_All_DOC = "/get-all";



    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    //Arizachiga tegishli bolgan tekshirilgan arizalar
    @GetMapping(GET_COMPLETED_APPLICATIONDOC_BY_APPLICANT)
    public HttpEntity<?> getMyApplications(@CurrentUser CustomUserDetails userDetails,
                                           @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                           @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size) {
        return ResponseEntity.ok(new ApiResponse("You applications", true, documentService.getCheckedApplication(page, size, userDetails.getUser())));
    }


    @PreAuthorize("hasAnyAuthority('SUPER_MODERATOR','MODERATOR','SUPER_MODERATOR_AND_MODERATOR','ADMIN')")
    @GetMapping(GET_BY_STATUS)
    public HttpEntity<?> getByStatus(@CurrentUser CustomUserDetails userDetails,
                                     @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                     @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size,
                                     @RequestParam DocumentStatus status
                                     ) {
        return ResponseEntity.ok(new ApiResponse("You applications", true, documentService.findAllByPageable(page, size,status, userDetails.getUser())));
    }

    // Hech kim tekshirmayotgan arizalar
    @GetMapping(GET_APPLICATIONS_LISTENER_IS_NULL)
    public ResponseEntity<?> getAllApplicationListenerIsNull(@RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                                             @RequestParam String sts,
                                                             @CurrentUser CustomUserDetails user,
                                                             @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size) {
        return ResponseEntity.ok(documentService.getAllApplicationListenerIsNull(page, size,user.getUser(),sts));
    }

    @GetMapping(GET_BY_CHECKED_BY_LISTENER)
    public ResponseEntity<?> getAllApplicationListenerIsNotNul(@RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                                             @RequestParam String sts,
                                                             @CurrentUser CustomUserDetails user,
                                                             @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size) {
        return ResponseEntity.ok(documentService.getAllApplicationListenerIsNull(page, size,user.getUser(),sts));
    }

    // Hech kim teshrimayotgan arizaga Listener tayinlaydi
    @PutMapping(SET_LISTENER)
    public HttpEntity<?> setListener(@RequestParam(name = "documentId") UUID documentId, @RequestParam(name = "listenerId") UUID listenerId) {
        return ResponseEntity.ok(documentService.changeListener(documentId, listenerId));
    }
    @PutMapping(SET_SECTION)
    public HttpEntity<?> setSection(@RequestParam(name = "documentId") UUID documentId, @RequestParam(name = "sectionId") Long sectionId) {
        return ResponseEntity.ok(documentService.changeSection(documentId, sectionId));
    }

    // Rad etilgan ariza to'grisida
    @GetMapping(GET_DOCUMENT_BY_APPLICATION)
    public ResponseEntity<?> getDocumentByApplication(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getByApplication(id));
    }

    // Answer biriktirilgan documentni boshliqqa va arizachiga yuborish
    @GetMapping(GET_DOCUMENT_APPLICATION_TO_SEND)
    public ResponseEntity<?> getDocumentByApplication(@CurrentUser CustomUserDetails userDetails, Pageable pageable) {
        return ResponseEntity.ok(documentService.getAllDocumentToSend(userDetails.getUser(), pageable));
    }

    //Bo'lim boshlig'iga kelgan arizalar
    @GetMapping(GET_BOSS_DOCUMENTS)
    public ResponseEntity<?> getBossAnwers(@CurrentUser CustomUserDetails userDetails,
                                           @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                           @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size,
                                           @RequestParam(name = "search",defaultValue = "") String search
                                           ) {
        if (!search.equals("")){
            return ResponseEntity.ok(documentService.getBossAnswers(userDetails.getUser(),search, page, size));
        }else {
            return ResponseEntity.ok(documentService.getBossAnswers(userDetails.getUser(), page, size));
        }
    }

    // Listener tekshirgan arizalar
    @GetMapping(CHECKED_BY_LISTENER)
    public HttpEntity<?> getAllCheckedByListener(@CurrentUser CustomUserDetails userDetails,
                                                 @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                                 @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size) {
        return ResponseEntity.ok(documentService.getAllCheckedByListener(page, size, userDetails.getUser()));
    }

    // Bo'lim boshlig'i javobni qoniqarli deb tasdiqlagan
    @PutMapping(ACCEPT_BY_BOSS)
    public ResponseEntity<?> setCompleted(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.confirmByBoss(id));
    }

    //bo'lim boshlig'i qodiqarsiz deb belgilagan answer
    @PutMapping(DENIED_BY_BOSS)
    public ResponseEntity<?> deniedAnswer(@PathVariable UUID id, @RequestBody Commit comment) {
        return ResponseEntity.ok(documentService.denied(id, comment.getComment()));

    }

    // boshliq tomonidan qodiqarsiz deb topilgan answer documentini olish
    @GetMapping(GET_DENIED_DOCUMENT)
    public ResponseEntity<?> getDeniedDocument(@CurrentUser CustomUserDetails userDetails,
                                               @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                               @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size) {
        return ResponseEntity.ok(documentService.deniedAnswerDocument(userDetails.getUser(), CommonUtils.getPageable(page,size)));
    }


    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(GET_LIST)
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(documentService.findAll());
    }

    @PreAuthorize("hasAnyAuthority('SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @GetMapping(GET_ACCEPTED_LIST)
    public ResponseEntity<?> getAllAccepted(Pageable pageable) {
        return ResponseEntity.ok(documentService.getAlllAcceptedDocument(pageable));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_MODERATOR','MODERATOR','SUPER_MODERATOR_AND_MODERATOR','LISTENER')")
    @GetMapping(GET_ACCEPTED_APPLICATION_BY_SECTION)
    public ResponseEntity<?> getAllAcceptedApplicationBySection(@CurrentUser CustomUserDetails userDetails, Pageable pageable) {
        return ResponseEntity.ok(documentService.getAlllAcceptedApplication(userDetails.getUser(), pageable));
    }

    @GetMapping(GET_FEEDBACK)
    public ResponseEntity<?> getFeedback(@CurrentUser CustomUserDetails userDetails, Pageable pageable) {
        return ResponseEntity.ok(documentService.getAnswerFeedback(userDetails.getUser(), pageable));
    }

    @GetMapping(GET_All_DOC)
    public ResponseEntity<?> getAll(@CurrentUser CustomUserDetails userDetails,
                                    @RequestParam(name = "search",defaultValue = "") String search,
                                    @RequestParam(name="status",defaultValue = "ALL") DocumentStatus status,
                                    @RequestParam(name="page",defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                    @RequestParam(name="size",defaultValue = AppConstants.DEFAULT_SIZE) int size

                                    ) {
        return ResponseEntity.ok(documentService.getAllDocs(search,userDetails.getUser(),status, CommonUtils.getPageable(page,size)));
    }
}

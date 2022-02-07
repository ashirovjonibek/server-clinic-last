package uz.napa.clinic.controller;

import javassist.CannotCompileException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.payload.*;
import uz.napa.clinic.repository.RoleRepository;
import uz.napa.clinic.repository.SectionRepository;
import uz.napa.clinic.repository.UserRepository;
import uz.napa.clinic.security.CurrentUser;
import uz.napa.clinic.security.CustomUserDetails;
import uz.napa.clinic.service.UserService;
import uz.napa.clinic.utils.AppConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
public class AuthController {
    private static final String REGISTER_LISTENER = "/createListener";
    private static final String REGISTER_APPLICANT = "/createApplicant";
    private static final String LOGIN = "/login";
    private static final String UPDATE_LISTENER = "updateListener/{id}";
    private static final String UPDATE_LISTENER_BY_VIEW = "updateListenerByView/{id}";
    private static final String UPDATE_APPLICANT = "updateApplicant/{id}";
    private static final String CURRENT_USER = "/me";
    private static final String APPLICANT_LIST = "/applicants";
    private static final String LISTENER_LIST_BY_SECTION = "/listenerBySection";
    private static final String LISTENER_LIST = "/listeners";
    private static final String LISTENER_LIST_VIEW_FALSE = "/listeners/view-false";
    private static final String ROLE_LIST = "/roles";
    private static final String UPDATE_LISTENER_BY_ROLE = "/update/listenerByRole";
    private static final String GET_BOSS = "/bosses";
    private static final String GET_MODERATOR = "/moderators";
    private static final String GET_SUPER_MODERATOR_AND_MODERATOR = "/moderatorAndSuperModerator";
    private static final String DELETE = "/delete";
    private static final String BLOCK = "/block";
    private static final String GET_LISTENER_RATING = "/listener/rating";
    private static final String RESET_PASSWORD = "/resetPassword";
    private static final String SAVE_PASSWORD = "/savePassword";
    private static final String SET_PROFILE = "/setProfile";
    private static final String CHECK_SECRET_CODE = "/check-phone";
    private static final String RE_SEND_SECRET_CODE = "/re-send-secret-code";

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;

    public AuthController(UserService userService, RoleRepository roleRepository, UserRepository userRepository, SectionRepository sectionRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.sectionRepository = sectionRepository;
    }

    //Userlarni login qilish
    @PostMapping(LOGIN)
    public HttpEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    //Arizachi qoshish
    @PostMapping(REGISTER_APPLICANT)
    public HttpEntity<?> registerApplicant(@RequestBody ApplicantRequest request) {
        if (!userRepository.existsByPhoneNumber(request.getPhoneNumber())&&!userRepository.existsByEmail(request.getEmail())){
            ApiResponse response = userService.addApplicant(request);
            return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(response);
        }
        else {
            Map<String,String> errors=new HashMap<>();
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())){
                errors.put("phoneNumber","The phone number is already available");
            }
            if (userRepository.existsByEmail(request.getEmail())){
                errors.put("email","Email is already available");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("error",false,errors));
        }
    }

    //Prokuratura xodimlarini royxatdan otkazish
    @PostMapping(REGISTER_LISTENER)
    public HttpEntity<?> registerListener(@RequestBody ListenerRequest request) throws CannotCompileException {
        if (!userRepository.existsByPhoneNumber(request.getPhoneNumber())&&!userRepository.existsByEmail(request.getEmail())){
            ApiResponse response = userService.addListener(request);
            return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(response);
        }else {
            Map<String,String> errors=new HashMap<>();
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())){
                errors.put("phoneNumber","The phone number is already available");
            }
            if (userRepository.existsByEmail(request.getEmail())){
                errors.put("email","Email is already available");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("error",false,errors));
        }
    }


    //Arizachi ma'lumotlarini o'zgartirish
    @PutMapping(UPDATE_APPLICANT)
    public HttpEntity<?> updateApplicant(@PathVariable UUID id, @RequestBody ApplicantRequest request) {
        ApiResponse response = userService.updateApplicant(id, request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    //Listener ma'lumotlarini o'zgartirish
    @PutMapping(UPDATE_LISTENER)
    public HttpEntity<?> updateListener(@PathVariable UUID id, @RequestBody ListenerRequest request) {
        ApiResponse response = userService.updateListener(id, request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    //Admin tomonidan yangi qoshilgan Listenerlar korildi
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(UPDATE_LISTENER_BY_VIEW)
    public void updateListener(@PathVariable UUID id) {
        userService.updateListenerByView(id);
    }


    //Current user
    @GetMapping(CURRENT_USER)
    public HttpEntity<?> getCurrentUser(@CurrentUser CustomUserDetails user) {
        return ResponseEntity.ok(new ApiResponse("User", true, UserResponse.fromEntity(user)));
    }

    //Applicant List
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @GetMapping(APPLICANT_LIST)
    public HttpEntity<?> getApplicantList(@RequestParam(name = "size",defaultValue = AppConstants.DEFAULT_SIZE) int size,
                                          @RequestParam(name = "page",defaultValue = AppConstants.DEFAULT_PAGE) int page
    ) {
        return ResponseEntity.ok(userService.applicantList(size,page));
    }


    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @GetMapping(ROLE_LIST)
    public HttpEntity<?> getRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    //Royxatdan o'tgan xodimlarga role berish
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @PutMapping(UPDATE_LISTENER_BY_ROLE)
    public ResponseEntity<?> updateListener(@RequestParam Long roleId,
                                            @RequestParam(required = false) Long sectionId,
                                            @RequestParam UUID userId) {
        ApiResponse response = userService.updateListenerByRole(roleId,sectionId, userId);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    //Listenerlar royxatini kafedra ID si boyicha olish
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @GetMapping(LISTENER_LIST_BY_SECTION)
    public HttpEntity<?> getListenerListBySection(@RequestParam Long sectionId) {
        return ResponseEntity.ok(userService.listenerList(UserStatus.LISTENER, sectionId));
    }

    //Listenerlar listini olish
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @GetMapping(LISTENER_LIST)
    public HttpEntity<?> getListenerList(@RequestParam(name = "size",defaultValue = AppConstants.DEFAULT_SIZE) int size,
                                         @RequestParam(name = "page",defaultValue = AppConstants.DEFAULT_PAGE) int page
    ) {
        return ResponseEntity.ok(userService.userList(UserStatus.LISTENER,page,size));
    }

    @GetMapping(LISTENER_LIST_VIEW_FALSE)
    public HttpEntity<?> getListenerList1() {
        return ResponseEntity.ok(userService.userListViewFalse(UserStatus.LISTENER));
    }

    //Bo'lim boshliqlari listini olish
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @GetMapping(GET_BOSS)
    public ResponseEntity<?> getBoss(@RequestParam(name = "size",defaultValue = AppConstants.DEFAULT_SIZE) int size,
                                     @RequestParam(name = "page",defaultValue = AppConstants.DEFAULT_PAGE) int page
    ) {
        return ResponseEntity.ok(userService.userList(UserStatus.MODERATOR,page,size));
    }

    //Moderatorlar listini olish
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(GET_MODERATOR)
    public ResponseEntity<?> getModerators(@RequestParam(name = "size",defaultValue = AppConstants.DEFAULT_SIZE) int size,
                                           @RequestParam(name = "page",defaultValue = AppConstants.DEFAULT_PAGE) int page
    ) {
        return ResponseEntity.ok(userService.userList(UserStatus.SUPER_MODERATOR,page,size));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(GET_SUPER_MODERATOR_AND_MODERATOR)
    public ResponseEntity<?> getHybrid(@RequestParam(name = "size",defaultValue = AppConstants.DEFAULT_SIZE) int size,
                                       @RequestParam(name = "page",defaultValue = AppConstants.DEFAULT_PAGE) int page
    ) {
        return ResponseEntity.ok(userService.userList(UserStatus.SUPER_MODERATOR_AND_MODERATOR,page,size));
    }

    //Userlarni o'chirish
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @DeleteMapping(DELETE)
    public ResponseEntity<?> trashUser(@RequestParam UUID id) {
        return ResponseEntity.ok(userService.trash(id));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @PutMapping(BLOCK)
    public ResponseEntity<?> blockUser(@RequestParam UUID id) {
        return ResponseEntity.ok(userService.block(id));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_MODERATOR','ADMIN','MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @GetMapping(GET_LISTENER_RATING)
    public ResponseEntity<?> getRating() {
        return ResponseEntity.ok(userService.getListenerRating());
    }

    @PostMapping(RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(HttpServletRequest request,
                                           @RequestParam("phone") String phone) {
        return ResponseEntity.ok(userService.resetPassword(request, phone));
    }

    @PostMapping(SAVE_PASSWORD)
    public ResponseEntity<?> savePassword(@RequestBody PasswordRequest request) {
        return ResponseEntity.ok(userService.savePassword(request));
    }

    @PostMapping(SET_PROFILE)
    public ResponseEntity<?> setProfile(@CurrentUser CustomUserDetails user, @RequestBody ResetUser resetUser) {
        return ResponseEntity.ok(userService.setProfile(user.getUser(),resetUser));
    }

    @CrossOrigin("*")
    @PostMapping(CHECK_SECRET_CODE)
    public ResponseEntity<?> checkPhone(@RequestBody CheckPhoneRequest request) {
        return ResponseEntity.ok(userService.checkCode(request));
    }

    @CrossOrigin("*")
    @GetMapping(RE_SEND_SECRET_CODE)
    public ResponseEntity<?> checkPhone(@RequestParam String phone) {
        return ResponseEntity.ok(userService.reSendCode(phone));
    }
}

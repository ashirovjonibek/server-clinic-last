package uz.napa.clinic.service.iml;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.napa.clinic.entity.*;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.exception.BadRequestException;
import uz.napa.clinic.payload.*;
import uz.napa.clinic.projection.ListenerRating;
import uz.napa.clinic.repository.*;
import uz.napa.clinic.security.CustomUserDetails;
import uz.napa.clinic.security.JwtTokenProvider;
import uz.napa.clinic.service.UserService;
import uz.napa.clinic.service.iml.helper.HtmlConverter;
import uz.napa.clinic.utils.CommonUtils;

import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AnswerRepository answerRepository;
    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final DistrictRepository districtRepository;
    private final SocialStatusRepository socialStatusRepository;
    private final AttachmentRepository attachmentRepository;
    private final EskizServise eskizServise;
    private final SecretCodeRepository secretCodeRepository;
    private final SectionRepository sectionRepository;


    public UserServiceImpl(UserRepository userRepository, EntityManager entityManager, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, AnswerRepository answerRepository, JavaMailSender mailSender, PasswordResetTokenRepository passwordResetTokenRepository, DistrictRepository districtRepository, PositionRepository positionRepository, SocialStatusRepository socialStatusRepository, AttachmentRepository attachmentRepository, EskizServise eskizServise, SecretCodeRepository secretCodeRepository, SectionRepository sectionRepository) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.answerRepository = answerRepository;
        this.mailSender = mailSender;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.districtRepository = districtRepository;
        this.socialStatusRepository = socialStatusRepository;
        this.attachmentRepository = attachmentRepository;
        this.eskizServise = eskizServise;
        this.secretCodeRepository = secretCodeRepository;
        this.sectionRepository = sectionRepository;
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        if (!loginRequest.getPhoneNumber().startsWith("+998"))
            {
                User user=userRepository.findByEmail(loginRequest.getPhoneNumber()).get();
            if (user!=null)
            loginRequest.setPhoneNumber(user.getPhoneNumber());
            else
                throw new BadCredentialsException("User not found with email");
            }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNumber(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

        if (user.isDeleted()) {
            throw new BadRequestException("You blocked By Admin");
        }
        if (!user.isViewed()&&user.getStatus().equals(UserStatus.LISTENER)) {
            throw new BadRequestException("User not viewed");
        }
        if (!user.isCheckPhone()&&!user.getStatus().equals(UserStatus.ADMIN)){
            throw new BadRequestException(user.getPhoneNumber()+"-Your phone not checked");
        }
        String token = jwtTokenProvider.generateToken(authentication);
        return new JwtResponse(token);


    }

    @Override
    public ApiResponse trash(UUID id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) {
            User user = findUser.get();
            user.setDeleted(true);
            userRepository.save(user);
            return new ApiResponse("User Deleted with ID " + id, true);
        } else {
            throw new BadRequestException("User Not found with ID " + id);
        }
    }

    @Override
    public ApiResponse block(UUID id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) {
            User user = findUser.get();
            if (!user.isBlocked()) {
                user.setBlocked(true);
                userRepository.save(user);
                return new ApiResponse("User blocked with ID " + id, true);
            } else {
                user.setBlocked(false);
                userRepository.save(user);
                return new ApiResponse("User unblocked with ID " + id, true);
            }

        } else {
            throw new BadRequestException("User Not found with ID " + id);
        }
    }

    @Override
    public ApiResponse addApplicant(ApplicantRequest request) {
        if (!userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            if (validBirthDate(request.getBirthDate())) {
                User user = new User();
                user.setFullName(request.getFullName());
                user.setGender(request.getGender());
                user.setBirthDate(request.getBirthDate());
                user.setDistrict(entityManager.getReference(District.class, request.getDistrictId()));
                user.setAddress(request.getAddress());
                user.setEmail(request.getEmail());
                user.setPhoneNumber(request.getPhoneNumber());
                if (request.getSocialStatusId()!=null)user.setSocialStatus(entityManager.getReference(SocialStatus.class, request.getSocialStatusId()));
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setStatus(UserStatus.APPLICANT);
                user.setRoles(Collections.singletonList(roleRepository.findByName("USER")));

                SecretCode secretCode=new SecretCode();
                secretCode.setPhoneNumber(request.getPhoneNumber());
                secretCode.setCode(UUID.randomUUID().toString().substring(0,6));
                secretCode.setStatus(true);
                try{
                    eskizServise.sendSms(request.getPhoneNumber(),secretCode.getCode()+" bu sizning bir martalik parolingiz.");
                    secretCodeRepository.save(secretCode);
                    userRepository.save(user);
                    Map<String,String> responseMessage=new HashMap<>();
                    responseMessage.put("message","We send code your phone!");
                    return new ApiResponse("User Successfully created !!!", true,responseMessage);
                }catch (Exception e){
                    e.printStackTrace();
                    Map<String,String> errors=new HashMap<>();
                    errors.put("phoneNumber","Error send code your phone number!. Please check your number and try again.");
                    return new ApiResponse("error",false,errors);
                }

            } else {
                throw new BadRequestException("Not match Applicant age! ");
            }
        } else {
            throw new BadRequestException("Data Already Exist !!!");
        }
    }

    @Override
    public ApiResponse addListener(ListenerRequest request) {
        if (!userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            if (validBirthDate(request.getBirthDate())) {
                User user = new User();
                user.setFullName(request.getFullName());
                user.setSection(entityManager.getReference(Section.class, request.getSectionId()));
                user.setPhoneNumber(request.getPhoneNumber());
                user.setEmail(request.getEmail());
                user.setDistrict(entityManager.getReference(District.class, request.getDistrictId()));
                user.setStatus(UserStatus.LISTENER);
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setBirthDate(request.getBirthDate());
                user.setAddress(request.getAddress());
                user.setRoles(Collections.singletonList(roleRepository.findByName("LISTENER")));
                SecretCode secretCode=new SecretCode();
                secretCode.setPhoneNumber(request.getPhoneNumber());
                secretCode.setCode(UUID.randomUUID().toString().substring(0,6));
                secretCode.setStatus(true);
                try{
                    eskizServise.sendSms(request.getPhoneNumber(),secretCode.getCode()+" bu sizning bir martalik parolingiz.");
                    secretCodeRepository.save(secretCode);
                    userRepository.save(user);
                    Map<String,String> responseMessage=new HashMap<>();
                    responseMessage.put("message","We send code your phone!");
                    return new ApiResponse("User Successfully created !!!", true,responseMessage);
                }catch (Exception e){
                    e.printStackTrace();
                    Map<String,String> errors=new HashMap<>();
                    errors.put("phoneNumber","Error send code your phone number!. Please check your number and try again.");
                    return new ApiResponse("error",false, errors);
                }
            } else {
                throw new BadRequestException("Not match Listener age! ");
            }
        } else {
            throw new BadRequestException("Data Already Exist !!!");
        }
    }

    @Override
    public ApiResponse updateApplicant(UUID id, ApplicantRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (validBirthDate(request.getBirthDate())) {
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                userRepository.save(fillApplicant(user, request));
                return new ApiResponse("Applicant successfully updated !", true);
            } else {
                throw new BadRequestException("Applicant Not Found with ID: " + id);
            }
        } else {
            throw new BadRequestException("Not match Applicant age ");
        }
    }

    @Override
    public ApiResponse updateListener(UUID id, ListenerRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (validBirthDate(request.getBirthDate())) {
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                userRepository.save(fillListener(user, request));
                return new ApiResponse("Listener successfully updated !", true);
            } else {
                throw new BadRequestException("Listener Not Found with ID: " + id);
            }
        } else {
            throw new BadRequestException("Not match Listener age! ");
        }
    }

    @Override
    public UserResponse getOne(UUID id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) {
            return UserResponse.fromEntity(new CustomUserDetails(findUser.get()));
        } else {
            throw new BadRequestException("User not Found with ID " + id);
        }
    }

    @Override
    public ApplicantResponse getApplicant(User user) {
        return ApplicantResponse.fromEntity(user);
    }

    @Override
    public ListenerResponse getListener(User user) {
        return ListenerResponse.fromEntity(user);
    }

    @Override
    public ApiResponse updateListenerByRole(Long roleId,Long sectionId, UUID userId) {
        Optional<User> findUser = userRepository.findById(userId);
        Optional<Role> findRole = roleRepository.findById(roleId);

        if (findUser.isPresent()) {
            if (findRole.isPresent()) {
                User user = findUser.get();
                Role role = findRole.get();
                if (role.getSystemName().toUpperCase().equals(UserStatus.MODERATOR.toString())) {
                    user.setStatus(UserStatus.MODERATOR);
                } else if (role.getSystemName().toUpperCase().equals(UserStatus.LISTENER.toString())) {
                    user.setStatus(UserStatus.LISTENER);
                } else if (role.getSystemName().toUpperCase().equals(UserStatus.APPLICANT.toString())) {
                    user.setStatus(UserStatus.APPLICANT);
                } else if (role.getSystemName().toUpperCase().equals(UserStatus.ADMIN.toString())) {
                    user.setStatus(UserStatus.ADMIN);
                } else if (role.getSystemName().toUpperCase().equals(UserStatus.SUPER_MODERATOR.toString())) {
                    user.setStatus(UserStatus.SUPER_MODERATOR);
                }else if (role.getSystemName().toUpperCase().equals(UserStatus.SUPER_MODERATOR_AND_MODERATOR.toString())) {
                    user.setStatus(UserStatus.SUPER_MODERATOR_AND_MODERATOR);
                }
                if (sectionId!=null){
                    Optional<Section> byId = sectionRepository.findById(sectionId);
                    if (byId.isPresent()){
                        user.setSection(byId.get());
                    }
                }
                List<Role> roles = new ArrayList<>(Collections.singletonList(findRole.get()));
                user.setRoles(roles);
                userRepository.save(user);
                return new ApiResponse("User Successfully Updated !", true);
            } else {
                throw new BadRequestException("Role not found !");
            }
        } else {
            throw new BadRequestException("User not found !");
        }
    }

    @Override
    public ResPageable applicantList(int size,int page) {
        Pageable pageable = CommonUtils.getPageable(page, size);
        Page<User> all = userRepository.findByStatusAndDeletedFalse(UserStatus.APPLICANT,pageable);
        return new ResPageable(
                all.getContent().stream().map(ApplicantResponse::fromEntity).collect(Collectors.toList()),
                page,
                all.getTotalPages(),
                all.getTotalElements()
        );
    }

    @Override
    public List<ListenerResponse> listenerList(UserStatus status, Long sectionId) {
        List<User> all = userRepository.findByStatusAndSectionAndDeletedFalse(status, entityManager.getReference(Section.class, sectionId));
        return all.stream().map(ListenerResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public ResPageable userList(UserStatus status,int page,int size) {
        Pageable pageable = CommonUtils.getPageable(page, size);
        Page<User> moderList = userRepository.findByStatusAndDeletedFalseAndViewedTrue(status,pageable);
        return new ResPageable(
                moderList.stream().map(ListenerResponse::fromEntity).collect(Collectors.toList()),
                page,
                moderList.getTotalPages(),
                moderList.getTotalElements()
        );
    }

    public List<ListenerResponse> userListViewFalse(UserStatus status) {
        List<User> moderList = userRepository.findByStatusAndDeletedFalseAndViewedFalse(status);
        return moderList.stream().map(ListenerResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void updateListenerByView(UUID id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) {
            User user = findUser.get();
            user.setViewed(true);
            userRepository.save(user);
        } else {
            throw new BadRequestException("User not Found with ID " + id);
        }
    }

    @Override
    public List<ListenerResponse> getListenerRating() {
        List<ListenerResponse> list = new ArrayList<>();
        List<ListenerRating> listenerRating = userRepository.getListenerRating();
        for (ListenerRating rating : listenerRating) {
            Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(rating.getNumber());
            byPhoneNumber.ifPresent(user -> {
                ListenerResponse listenerResponse = ListenerResponse.fromEntity(byPhoneNumber.get());
                int dislikeCount = answerRepository.getDislikeCount(user);
                if (rating.getCount() != 0) {
                    if (dislikeCount != 0) {
                        int stars = (dislikeCount * 100) / rating.getCount();
                        if (stars > 85) {
                            listenerResponse.setStars(5);
                        } else if (stars < 86 && stars > 70) {
                            listenerResponse.setStars(4);
                        } else if (stars < 71 && stars > 55) {
                            listenerResponse.setStars(3);
                        } else if (stars < 56 && stars > 44) {
                            listenerResponse.setStars(2);
                        } else {
                            listenerResponse.setStars(1);
                        }
                    } else {
                        listenerResponse.setStars(5);
                    }
                } else {
                    listenerResponse.setStars(0);
                }
                list.add(listenerResponse);
            });

        }
        return list;
    }

    @Override
    public ApiResponse resetPassword(HttpServletRequest request, String phone) {
//        String p;
//        if (!phone.startsWith("+")) p=String.format("%s%s","+",phone);
//        else p=phone;
        Optional<User> user = userRepository.findByPhoneNumber(phone);
        Long expireTime=new Date().getTime()+300000;
        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            user.get().setPassword(passwordEncoder.encode(token.substring(0,token.indexOf("-"))));
            resetToken.setUser(user.get());
            resetToken.setExpiryDate(new Date(expireTime));
            eskizServise.sendSms(phone,token.substring(0,token.indexOf("-"))+
                    " bu accountga kirish uchun parolingiz. Acountga kirgach parolni yangilashingizni so'raymiz!");
            userRepository.save(user.get());
            passwordResetTokenRepository.save(resetToken);
            return new ApiResponse("We send password to " + phone, true);
        } else {
            throw new BadRequestException("User not found with number :" + phone);
        }
    }

    @Override
    public ApiResponse savePassword(PasswordRequest request) {
        validatePasswordResetToken(request.getToken());
        Optional<PasswordResetToken> byToken = passwordResetTokenRepository.findByToken(request.getToken());
        if (byToken.isPresent()) {
            PasswordResetToken passwordResetToken = byToken.get();
            Optional<User> byId = userRepository.findById(passwordResetToken.getUser().getId());
            if (byId.isPresent()) {
                User user = byId.get();
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                return new ApiResponse("Password Successfully updated !", true);
            } else {
                throw new BadRequestException("User not found");
            }
        } else {
            throw new BadRequestException("User not found with token: " + request.getToken());
        }
    }

    @Override
    public ApiResponse setProfile(User user, ResetUser reqUser) {
        System.out.println(reqUser.getFullName());
        System.out.println(user.getFullName());
        System.out.println(user.getId());
        try{
            if(reqUser.getAddress()!=null&&!reqUser.getAddress().isEmpty())user.setAddress(reqUser.getAddress());
            if(reqUser.getBirthDate()!=null)user.setBirthDate(reqUser.getBirthDate());
            if(reqUser.getDistrictId()!=null&&reqUser.getDistrictId()!=0)user.setDistrict(districtRepository.findById(reqUser.getDistrictId()).orElseThrow(()->new IllegalStateException("District not found for set profile!!!")));
            if(reqUser.getEmail()!=null&&!reqUser.getEmail().isEmpty())user.setEmail(reqUser.getEmail());
            if(reqUser.getFullName()!=null&&!reqUser.getFullName().isEmpty())user.setFullName(reqUser.getFullName());
            if(reqUser.getGender()!=null&&!reqUser.getGender().isEmpty())user.setGender(reqUser.getGender());
            if(reqUser.getPassword()!=null&&!reqUser.getPassword().isEmpty())user.setPassword(passwordEncoder.encode(reqUser.getPassword()));
            if(reqUser.getPhoneNumber()!=null&&!reqUser.getPhoneNumber().isEmpty())user.setPhoneNumber(reqUser.getPhoneNumber());
            if (reqUser.getImageId()!=null)user.setAttachment(attachmentRepository.findById(reqUser.getImageId()).orElseThrow(()->
                    new IllegalStateException("Attachment not found for user")));
            if (reqUser.getSocialStatusId()!=null&&reqUser.getSocialStatusId()!=0)user
                    .setSocialStatus(
                            socialStatusRepository
                                    .findById(
                                            reqUser
                                                    .getSocialStatusId())
                                    .orElseThrow(()->new IllegalStateException("Social status not found for set profile!!!")));
            userRepository.save(user);
            return new ApiResponse("Profile successfully updated!!!",true);
        }catch (Exception e) {
            e.printStackTrace();

            return new ApiResponse("profile do not updated!!!",false);
        }
    }

    @Override
    public ApiResponse checkCode(CheckPhoneRequest request) {
        SecretCode byPhoneNumber = secretCodeRepository.findByPhoneNumber(request.getPhone());
        User user = userRepository.findByPhoneNumber(request.getPhone()).get();
        if (byPhoneNumber.getCode().equals(request.getCode())&&byPhoneNumber.isStatus()){
            user.setCheckPhone(true);
            secretCodeRepository.deleteById(byPhoneNumber.getId());
            userRepository.save(user);
            return new ApiResponse("User successfully registered!",true);
        }else {
            return new ApiResponse("Secret code not match!",false);
        }
    }

    @Override
    public ApiResponse reSendCode(String phone) {
        SecretCode byPhoneNumber = secretCodeRepository.findByPhoneNumber(phone);
        if (byPhoneNumber!=null){
            eskizServise.sendSms(byPhoneNumber.getPhoneNumber(),byPhoneNumber.getCode()+" bu sizning bir martalik parolingiz.");
            return new ApiResponse("success",true) ;
        }else {
            return new ApiResponse("error",false) ;
        }
    }

    private SimpleMailMessage constructResetTokenEmail(
            String contextPath, String token, User user) {
        String url = contextPath + "/api/auth/resetPassword?token=" + token;
        return constructEmail("Reset Password", url, user);
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private SimpleMailMessage constructEmail(String subject, String body,
                                             User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom("uzproclinica@gmail.com");
        return email;
    }

    public void validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passToken = passwordResetTokenRepository.findByToken(token);
        if (passToken.isPresent()) {
            if (isTokenExpired(passToken.get())) {
                throw new BadRequestException("Token is expired " + token);
            }
        } else {
            throw new BadRequestException("Invalid Token : " + token);
        }
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    public void sendEmail(String sendingEmail, String fullName, String token) {
        String link = "Ushbu kalit sizning parolni tiklash kalitingizdir: " + token+"\nUshbu kalitni kech kimga bermang!!!";
        String from = "uzproclinic@gmail.com";

//        try{
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(from);
//            message.setTo(sendingEmail);
//            message.setSubject("Qayta tiklash kaliti!!!");
//            message.setText(link);
//            mailSender.send(message);
//        }
        try {
            MimeMessage message1 = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message1);

            helper.setSubject("`Uz pro clinic` parolni tiklash uchun so'rov");
            helper.setFrom(from);
            helper.setTo(sendingEmail);

            boolean html = true;
            helper.setText(HtmlConverter.convert(fullName,token,"uz"), html);
            mailSender.send(message1);
        }
//        catch (Exception e) {
//
//        }
//
//        String body = "smth";
//        try {
//            String from1 = "islomxujanazarov0501@gmail.com";
//            MimeMessage message1 = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message1);
//            helper.setSubject("Reset Password");
//            helper.setFrom(from1);
//            helper.setTo(sendingEmail);
//            helper.setText(link, true);
//            mailSender.send(message);
//        }
        catch (Exception ignored) {
        }
    }


    private boolean validBirthDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        Integer birthDate = Integer.valueOf(simpleDateFormat.format(date));
        Integer now = Integer.valueOf(simpleDateFormat.format(new Date()));
        return now - birthDate >= 16;

    }

    private User fillListener(User user, ListenerRequest request) {
        user.setFullName(request.getFullName());
        user.setPosition(entityManager.getReference(Position.class, request.getPositionId()));
        user.setCourse(request.getCourse());
        user.setSection(entityManager.getReference(Section.class, request.getSectionId()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setDistrict(entityManager.getReference(District.class, request.getDistrictId()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBirthDate(request.getBirthDate());
        user.setAddress(request.getAddress());
        return user;
    }

    private User fillApplicant(User user, ApplicantRequest request) {
        user.setFullName(request.getFullName());
        user.setNation(entityManager.getReference(Nation.class, request.getNationId()));
        user.setGender(request.getGender());
        user.setBirthDate(request.getBirthDate());
        user.setDistrict(entityManager.getReference(District.class, request.getDistrictId()));
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setSocialStatus(entityManager.getReference(SocialStatus.class, request.getSocialStatusId()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return user;
    }

}

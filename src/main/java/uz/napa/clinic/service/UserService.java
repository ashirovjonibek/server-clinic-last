package uz.napa.clinic.service;

import uz.napa.clinic.entity.User;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.payload.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

public interface UserService {
    JwtResponse login(LoginRequest loginRequest);

    ApiResponse trash(UUID id);

    ApiResponse block(UUID id);

    ApiResponse addApplicant(ApplicantRequest request);

    ApiResponse addListener(ListenerRequest request);

    ApiResponse updateApplicant(UUID id, ApplicantRequest request);

    ApiResponse updateListener(UUID id, ListenerRequest request);

    UserResponse getOne(UUID id);

    ApplicantResponse getApplicant(User user);

    ListenerResponse getListener(User user);

    ApiResponse updateListenerByRole(Long roleId,Long sectionId, UUID userId);

    ResPageable applicantList(int size, int page);

    List<ListenerResponse> listenerList(UserStatus status, Long sectionId);

    ResPageable userList(UserStatus status, int page, int size);

    List<ListenerResponse> userListViewFalse(UserStatus status);

    void updateListenerByView(UUID id);

    List<ListenerResponse> getListenerRating();

    ApiResponse resetPassword(HttpServletRequest request, String email);

    ApiResponse savePassword(PasswordRequest request);

    ApiResponse setProfile(User user,ResetUser resetUser);

    ApiResponse checkCode(CheckPhoneRequest request);

    ApiResponse reSendCode(String phone);
}

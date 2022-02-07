package uz.napa.clinic.service;

import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.ResPageable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

public interface AttachmentService {

    ApiResponse uploadFile(MultipartHttpServletRequest request, User user);

    HttpEntity<?> getFileById(UUID id) throws MalformedURLException, IOException;

   HttpEntity<?> getVideoFile(UUID id) throws MalformedURLException;

    HttpEntity<?> getAudioFile(UUID id) throws MalformedURLException;

    ResPageable getNormativeLegalBase(int page, int size);

    ApiResponse delete(UUID id);
}

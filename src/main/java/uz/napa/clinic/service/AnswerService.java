package uz.napa.clinic.service;

import uz.napa.clinic.entity.Answer;
import uz.napa.clinic.payload.AnswerRequest;
import uz.napa.clinic.payload.AnswerResponse;
import uz.napa.clinic.payload.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface AnswerService {
    ApiResponse create(AnswerRequest request, UUID applicationId);

    ApiResponse update(AnswerRequest request, UUID id);

    ApiResponse updateWithDocument(AnswerRequest request, UUID documentId);

    AnswerResponse getOne(UUID id);

    ApiResponse delete(UUID id);

    List<AnswerResponse> list(UUID id);

    ApiResponse sendBoss(UUID id);

    ApiResponse sendApplicant(UUID id);

}

package uz.napa.clinic.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.napa.clinic.entity.enums.AnswerStatus;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.payload.AnswerRequest;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.repository.AnswerRepository;
import uz.napa.clinic.repository.UserRepository;
import uz.napa.clinic.security.CurrentUser;
import uz.napa.clinic.security.CustomUserDetails;
import uz.napa.clinic.service.iml.AnswerServiceImpl;

import java.util.Collections;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/answer")
public class AnswerController {
    private static final String CREATE = "/create";
    private static final String UPDATE = "/{id}";
    private static final String DELETE = "/{id}";
    private static final String GET_BY_ID = "/{id}";
    private static final String GET_BY_BOSS = "/{id}";
    private static final String GET_ALL_BY_ID = "/listener/{id}";
    private static final String UPDATE_DENIED_ANSWER = "/updateByDocument";
    private static final String SEND_BOSS = "/send/boss";
    private static final String SEND_APPLICANT = "/send/applicant";
    final
    AnswerServiceImpl answerService;
    final
    AnswerRepository answerRepository;
    final
    UserRepository userRepository;

    public AnswerController(AnswerServiceImpl answerService, AnswerRepository answerRepository, UserRepository userRepository) {
        this.answerService = answerService;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    //Javob yaratildi
    @PostMapping(CREATE)
    public HttpEntity<?> create(@RequestBody AnswerRequest request, @RequestParam UUID applicationId) {
        ApiResponse response = answerService.create(request, applicationId);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(response);
    }

    //boshliqqa jonatish
    @PutMapping(SEND_BOSS)
    public ResponseEntity<?> sendBoss(@RequestParam UUID answerId) {
        return ResponseEntity.ok(answerService.sendBoss(answerId));
    }

    //Arizachiga jonatish
    @PutMapping(SEND_APPLICANT)
    public ResponseEntity<?> sendApplicant(@RequestParam UUID answerId) {
        return ResponseEntity.ok(answerService.sendApplicant(answerId));
    }

    //Javobni ID boyicha olish
    @GetMapping(GET_BY_ID)
    public HttpEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(answerService.getOne(id));
    }


    //Javobni update qilish
    @PutMapping(UPDATE)
    public HttpEntity<?> update(@CurrentUser CustomUserDetails userDetails, @RequestBody AnswerRequest request, @PathVariable UUID id) {
        if (userDetails.getUser().getStatus().equals(UserStatus.APPLICANT)){
            request.setAttachmentId(Collections.emptyList());
            request.setDeniedMessage(null);
            request.setDescription(null);
            request.setStatus(AnswerStatus.COMPLETED);
        }else if (userDetails.getUser().getStatus().equals(UserStatus.LISTENER)){
            request.setComment(null);
            request.setLiked(true);
        }
        return ResponseEntity.ok(answerService.update(request, id));
    }

    //Denied bolgan answerni update qilish
    @PutMapping(UPDATE_DENIED_ANSWER)
    public HttpEntity<?> updateAnswerWithDocument(@RequestBody AnswerRequest request, @RequestParam UUID documentId) {
        return ResponseEntity.ok(answerService.updateWithDocument(request, documentId));
    }

}

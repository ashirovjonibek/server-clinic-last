package uz.napa.clinic.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.repository.AttachmentTypeRepository;
import uz.napa.clinic.security.CurrentUser;
import uz.napa.clinic.security.CustomUserDetails;
import uz.napa.clinic.service.iml.AttachmentServiceImpl;
import uz.napa.clinic.utils.AppConstants;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/attach")
public class AttachmentController {
    private static final String CREATE = "/upload";
    private static final String GET_BY_ID = "/{id}";
    private static final String GET_BY_ID_VIDEO = "/video/{id}";
    private static final String GET_BY_ID_AUDIO = "/audio/{id}";
    private static final String NORMATIVE_LEGAL_BASE = "/normative-legal-base";
    private static final String DOC_FOR_APPLICANT = "/doc-for-applicant";
    final
    AttachmentServiceImpl attachmentService;
    final
    AttachmentTypeRepository attachmentTypeRepository;

    public AttachmentController(AttachmentServiceImpl attachmentService, AttachmentTypeRepository attachmentTypeRepository) {
        this.attachmentService = attachmentService;
        this.attachmentTypeRepository = attachmentTypeRepository;
    }

    @PostMapping(CREATE)
    public HttpEntity<?> uploadFile(@CurrentUser CustomUserDetails userDetails, MultipartHttpServletRequest file) throws IOException {
        System.out.println(userDetails.getUser().getFullName());
        ApiResponse response = attachmentService.uploadFile(file, userDetails.getUser());
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(response);
    }


    @GetMapping(GET_BY_ID)
    public HttpEntity<?> getFileById(@PathVariable UUID id) throws IOException {
        return attachmentService.getFileById(id);
    }

    @GetMapping(DOC_FOR_APPLICANT)
    public HttpEntity<?> getDoc() throws IOException {
        Resource resource = new ClassPathResource("/file/file.docx");
        File file = resource.getFile();
        byte[] bytes = Files.readAllBytes(file.toPath());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/word"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.docx")
                .body(bytes);
    }

    @GetMapping(GET_BY_ID_VIDEO)
    public HttpEntity<?> getVideoFileById(@PathVariable UUID id) throws MalformedURLException {
        return attachmentService.getVideoFile(id);
    }

    @GetMapping(GET_BY_ID_AUDIO)
    public HttpEntity<?> getAudioFileById(@PathVariable UUID id) throws MalformedURLException {
        return attachmentService.getAudioFile(id);
    }

    @GetMapping(NORMATIVE_LEGAL_BASE)
    public HttpEntity<?> getNormativeLegalBase(@RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                               @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_SIZE) int size) {
        return ResponseEntity.ok(attachmentService.getNormativeLegalBase(page, size));
    }

    @DeleteMapping
    public HttpEntity<?> deleteFile(@RequestParam UUID id) {

        return ResponseEntity.ok(
                attachmentService.delete(id)
        );
    }
}

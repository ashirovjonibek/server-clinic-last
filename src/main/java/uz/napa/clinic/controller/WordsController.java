package uz.napa.clinic.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.napa.clinic.entity.SocialStatus;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.payload.WordRequest;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.repository.SocialStatusRepository;
import uz.napa.clinic.security.CurrentUser;
import uz.napa.clinic.security.CustomUserDetails;
import uz.napa.clinic.service.iml.WordsServiceImpl;
import uz.napa.clinic.utils.AppConstants;
@RestController
@CrossOrigin("*")
@RequestMapping("/api/words")
public class WordsController {

    private static final String GET_ALL = "";
    private static final String DELETE = "/{id}";
    private static final String SOCIAL_STATUS="/social-status";

    final
    WordsServiceImpl wordsService;
     private final SocialStatusRepository socialStatusRepository;

    public WordsController(WordsServiceImpl wordsService, SocialStatusRepository socialStatusRepository) {
        this.wordsService = wordsService;
        this.socialStatusRepository = socialStatusRepository;
    }

    @PostMapping
    public HttpEntity<?> create(@RequestBody WordRequest words) {
        ApiResponse response = wordsService.create(words);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(response);
    }

    @PutMapping
    public HttpEntity<?> update(@RequestBody WordRequest words) {
        ApiResponse update = wordsService.update(words);
        return ResponseEntity.status(update.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(update);
    }

    @GetMapping(GET_ALL)
    public HttpEntity<?> getWordsList(@RequestParam(name = "page",defaultValue = AppConstants.DEFAULT_PAGE) int page,
                                      @RequestParam(name = "size",defaultValue = AppConstants.DEFAULT_SIZE) int size) {
        return ResponseEntity.ok(wordsService.list(page,size));
    }

    @DeleteMapping(DELETE)
    public HttpEntity<?> delete(@PathVariable Long id) {
        ApiResponse delete = wordsService.delete(id);
        return ResponseEntity.ok(delete);
    }


    @GetMapping(SOCIAL_STATUS)
    public HttpEntity<?> getAllSocial(){
        return ResponseEntity.ok(new ApiResponse("success",true,socialStatusRepository.findAll()));
    }

    @PostMapping(SOCIAL_STATUS)
    public HttpEntity<?> saveSocial(@CurrentUser CustomUserDetails userDetails, @RequestBody SocialStatus socialStatus){
        if (userDetails.getUser().getStatus().equals(UserStatus.ADMIN)){
            return ResponseEntity.ok(new ApiResponse("success",true,socialStatusRepository.save(socialStatus)));
        }else {
            return ResponseEntity.ok(new ApiResponse("Action not permit",false));
        }
    }

    @PutMapping(SOCIAL_STATUS)
    public HttpEntity<?> editSocial(@CurrentUser CustomUserDetails userDetails, @RequestBody SocialStatus socialStatus){
        if (userDetails.getUser().getStatus().equals(UserStatus.ADMIN)){
            return ResponseEntity.ok(new ApiResponse("success",true,socialStatusRepository.save(socialStatus)));
        }else {
            return ResponseEntity.ok(new ApiResponse("Action not permit",false));
        }
    }


    @DeleteMapping(SOCIAL_STATUS)
    public HttpEntity<?> deleteSocial(@CurrentUser CustomUserDetails userDetails, @RequestBody SocialStatus socialStatus){
        if (userDetails.getUser().getStatus().equals(UserStatus.ADMIN)){
            try{
                socialStatusRepository.delete(socialStatus);
                return ResponseEntity.ok(new ApiResponse("Social status deleted!",true));
            }catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.ok(new ApiResponse("Social status do not delete!",false));
            }

        }else {
            return ResponseEntity.ok(new ApiResponse("Action not permit",false));
        }
    }

}

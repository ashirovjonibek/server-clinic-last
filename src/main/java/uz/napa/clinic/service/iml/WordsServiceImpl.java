package uz.napa.clinic.service.iml;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import uz.napa.clinic.entity.Lang;
import uz.napa.clinic.entity.Words;
import uz.napa.clinic.payload.WordRequest;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.ResPageable;
import uz.napa.clinic.repository.LangRepository;
import uz.napa.clinic.repository.WordsRepository;
import uz.napa.clinic.service.WordsService;
import uz.napa.clinic.utils.CommonUtils;


@Service
public class WordsServiceImpl implements WordsService {
    private final WordsRepository wordsRepository;
    private final LangRepository langRepository;

    public WordsServiceImpl(WordsRepository wordsRepository, LangRepository langRepository) {
        this.wordsRepository = wordsRepository;
        this.langRepository = langRepository;
    }

    @Override
    public ApiResponse create(WordRequest request) {
        Words word=new Words();
        word.setName(langRepository.save(new Lang(
                request.getNameuz(),
                request.getNameuzCyr(),
                request.getNameru(),
                request.getNameen()
        )));
        word.setUrl(langRepository.save(new Lang(
                request.getUrluz(),
                request.getUrluzCyr(),
                request.getUrlru(),
                request.getUrlen()
        )));
        wordsRepository.save(word);
        return new ApiResponse("So'z kiritildi !", true);
    }

    @Override
    public Words getById(Long id) {
        return wordsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Data not found"));
    }

    @Override
    public ApiResponse delete(Long id) {
        if (getById(id) != null) {
            wordsRepository.deleteById(id);
            return new ApiResponse("So'z o'chirildi !", false);
        }
        return new ApiResponse("So'z topilmadi !", false);
    }

    @Override
    public ApiResponse update(WordRequest request) {
        Words words = getById(request.getId());
        words.getName().setUz(request.getNameuz());
        words.getName().setRu(request.getNameru());
        words.getName().setEn(request.getNameen());
        words.getUrl().setUz(request.getUrluz());
        words.getUrl().setRu(request.getUrlru());
        words.getUrl().setEn(request.getUrlen());
        wordsRepository.save(words);
        return new ApiResponse("So'z o'zgartirildi !", true);
    }

    @Override
    public ResPageable list(int page, int size) {
        Pageable pageable = CommonUtils.getPageable(page, size);
        Page<Words> all = wordsRepository.findAll(pageable);
        return new ResPageable(
                all.getContent(),
                page,
                all.getTotalPages(),
                all.getTotalElements()
        );
    }
}

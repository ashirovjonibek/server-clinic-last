package uz.napa.clinic.service;

import uz.napa.clinic.entity.Words;
import uz.napa.clinic.payload.WordRequest;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.ResPageable;

public interface WordsService {
    ApiResponse create(WordRequest request) ;

    Words getById(Long id);

    ApiResponse delete(Long id);

    ApiResponse update(WordRequest request) ;

    ResPageable list(int page, int size);
}

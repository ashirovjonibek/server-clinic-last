package uz.napa.clinic.service;

import uz.napa.clinic.entity.Section;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.SectionRequest;
import uz.napa.clinic.payload.SectionResponse;

import java.util.List;

public interface SectionService {

    ApiResponse create(SectionRequest request);

    SectionResponse getById(Long id);

    ApiResponse update(Long Id, SectionRequest request);

    ApiResponse delete(Long id);

    List<SectionResponse> list();
}

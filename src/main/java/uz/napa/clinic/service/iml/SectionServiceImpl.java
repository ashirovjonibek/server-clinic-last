package uz.napa.clinic.service.iml;

import org.springframework.stereotype.Service;
import uz.napa.clinic.entity.Section;
import uz.napa.clinic.exception.BadRequestException;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.SectionRequest;
import uz.napa.clinic.payload.SectionResponse;
import uz.napa.clinic.repository.SectionRepository;
import uz.napa.clinic.service.SectionService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;

    public SectionServiceImpl(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public ApiResponse create(SectionRequest request) {
        sectionRepository.save(fillSection(new Section(), request));
        return new ApiResponse("SuccessFully CREATED ", true);
    }

    @Override
    public SectionResponse getById(Long id) {
        Optional<Section> byId = sectionRepository.findById(id);
        if (byId.isPresent()) {
            return SectionResponse.fromEntity(byId.get());
        } else {
            throw new BadRequestException("Section not found with ID: " + id);
        }
    }

    @Override
    public ApiResponse update(Long Id, SectionRequest request) {
        Optional<Section> byId = sectionRepository.findById(Id);
        if (byId.isPresent()) {
            sectionRepository.save(fillSection(byId.get(), request));
            return new ApiResponse("Successfully updated ", true);
        } else {
            throw new BadRequestException("Section not found with ID: " + Id);
        }
    }

    @Override
    public ApiResponse delete(Long id) {
        Optional<Section> byId = sectionRepository.findById(id);
        if (byId.isPresent()) {
            sectionRepository.delete(byId.get());
            return new ApiResponse("Deleted ", true);
        } else {
            throw new BadRequestException("Section not found with ID: " + id);
        }
    }

    @Override
    public List<SectionResponse> list() {
        return sectionRepository.findAll().stream().map(SectionResponse::fromEntity).collect(Collectors.toList());
    }

    private Section fillSection(Section section, SectionRequest request) {
        section.setTitle(request.getTitle());
        if (request.getDescription() != null) {
            section.setDescription(request.getDescription());
        }
        return section;
    }
}

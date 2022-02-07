package uz.napa.clinic.service.iml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import uz.napa.clinic.entity.Position;
import uz.napa.clinic.exception.BadRequestException;
import uz.napa.clinic.payload.PositionRequest;
import uz.napa.clinic.payload.PositionResponse;
import uz.napa.clinic.repository.PositionRepository;
import uz.napa.clinic.service.PositionService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PositionServiceImpl implements PositionService {
    private final PositionRepository repository;

    public PositionServiceImpl(PositionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Position create(PositionRequest request) {
        Position position = new Position();
        position.setTitle(request.getTitle());
        if (request.getDescription() != null) {
            position.setDescription(request.getDescription());
        }
        return repository.save(position);
    }

    @Override
    public PositionResponse getById(Long id) {
        Optional<Position> byId = repository.findById(id);
        if (byId.isPresent()) {
            return PositionResponse.fromEntity(byId.get());
        } else {
            throw new BadRequestException("Position not found with ID: " + id);
        }
    }

    @Override
    public List<PositionResponse> list() {
        List<Position> all = repository.findAll();
        return all.stream().map(PositionResponse::fromEntity).collect(Collectors.toList());
    }

}

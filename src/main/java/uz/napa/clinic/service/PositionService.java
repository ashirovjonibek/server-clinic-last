package uz.napa.clinic.service;

import uz.napa.clinic.entity.Position;
import uz.napa.clinic.payload.PositionRequest;
import uz.napa.clinic.payload.PositionResponse;

import java.util.List;

public interface PositionService {
    Position create(PositionRequest request);

    PositionResponse getById(Long id);

    List<PositionResponse> list();
}

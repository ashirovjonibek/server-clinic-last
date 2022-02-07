package uz.napa.clinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.napa.clinic.payload.PositionRequest;
import uz.napa.clinic.service.iml.PositionServiceImpl;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/position")
public class PositionController {
    private static final String CREATE = "/create";
    private static final String GET_ALL = "";
    private static final String GET_BY_ID = "/{id}";

    final
    PositionServiceImpl positionService;

    public PositionController(PositionServiceImpl positionService) {
        this.positionService = positionService;
    }

    @PostMapping(CREATE)
    public HttpEntity<?> createPosition(PositionRequest request) {
        return ResponseEntity.ok(positionService.create(request));
    }

    @GetMapping(GET_ALL)
    public HttpEntity<?> getAll() {
        return ResponseEntity.ok(positionService.list());
    }

    @GetMapping(GET_BY_ID)
    public HttpEntity<?> getPositionById(@PathVariable Long id) {
        return ResponseEntity.ok(positionService.getById(id));
    }
}

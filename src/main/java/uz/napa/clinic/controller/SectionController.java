package uz.napa.clinic.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.napa.clinic.payload.SectionRequest;
import uz.napa.clinic.service.SectionService;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/section")

public class SectionController {
    private static final String UPDATE = "/{id}";
    private static final String GET_BY_ID = "/{id}";
    private static final String DELETE = "/{id}";

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @PostMapping
    public ResponseEntity<?> add(@RequestBody SectionRequest request) {
        return ResponseEntity.ok(sectionService.create(request));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @PutMapping(UPDATE)
    public ResponseEntity<?> update(@RequestBody SectionRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(sectionService.update(id, request));
    }

    @GetMapping(GET_BY_ID)
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getById(id));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_MODERATOR','SUPER_MODERATOR_AND_MODERATOR')")
    @DeleteMapping(DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.delete(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(sectionService.list());
    }


}

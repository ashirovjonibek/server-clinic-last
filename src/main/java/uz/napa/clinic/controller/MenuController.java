package uz.napa.clinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.napa.clinic.entity.Menu;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.MenuRequest;
import uz.napa.clinic.payload.MenuResponse;
import uz.napa.clinic.service.iml.MenuServiceImpl;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/menu")
public class MenuController {
    private static final String CREATE = "/create";
    private static final String UPDATE = "/{id}";
    private static final String GET_BY_ID = "/{id}";
    private static final String DELETE = "/{id}";
    private static final String GET_ALL = "";
    final
    MenuServiceImpl menuService;

    public MenuController(MenuServiceImpl menuService) {
        this.menuService = menuService;
    }

    @PostMapping(CREATE)
    public HttpEntity<?> create(@RequestBody MenuRequest request) {
        ApiResponse response = menuService.create(request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(response);
    }

    @PutMapping(UPDATE)
    public HttpEntity<?> update(@PathVariable Long id, @RequestBody MenuRequest request) {
        ApiResponse update = menuService.update(id, request);
        return ResponseEntity.status(update.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(update);
    }

    @GetMapping(GET_BY_ID)
    public HttpEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getById(id));
    }

    @GetMapping(GET_ALL)
    public HttpEntity<?> getList() {
        List<MenuResponse> list = menuService.list();
        return ResponseEntity.ok(new ApiResponse("List", true, list));
    }

    @DeleteMapping(DELETE)
    public HttpEntity<?> delete(@PathVariable Long id) {
        ApiResponse delete = menuService.delete(id);
        return ResponseEntity.ok(delete);
    }

}

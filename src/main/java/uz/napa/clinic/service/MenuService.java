package uz.napa.clinic.service;

import uz.napa.clinic.entity.Menu;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.MenuRequest;
import uz.napa.clinic.payload.MenuResponse;

import java.util.List;

public interface MenuService {
    ApiResponse create(MenuRequest request);

    MenuResponse getById(Long id);

    ApiResponse delete(Long id);

    ApiResponse update(Long id, MenuRequest request);

    List<MenuResponse> list();
}

package uz.napa.clinic.service.iml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.napa.clinic.entity.Menu;
import uz.napa.clinic.exception.BadRequestException;
import uz.napa.clinic.payload.ApiResponse;
import uz.napa.clinic.payload.ErrorResponse;
import uz.napa.clinic.payload.MenuRequest;
import uz.napa.clinic.payload.MenuResponse;
import uz.napa.clinic.repository.MenuRepository;
import uz.napa.clinic.service.MenuService;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    MenuRepository menuRepository;

    private final EntityManager entityManager;

    public MenuServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ApiResponse create(MenuRequest request) {
        try {
            menuRepository.save(fillMenu(new Menu(), request));
            return new ApiResponse("Menu Successfully Created !", true);
        } catch (Exception e) {
            throw new BadRequestException("Not to save.Wrong Data ");
        }
    }

    @Override
    public ApiResponse update(Long id, MenuRequest request) {
        Optional<Menu> findMenu = menuRepository.findById(id);
        if (findMenu.isPresent()) {
            menuRepository.save(fillMenu(findMenu.get(), request));
            return new ApiResponse("Menu o'zgartirildi !", true);
        } else {
            throw new BadRequestException("Menu Not Found with ID : " + id);
        }

    }

    @Override
    public MenuResponse getById(Long id) {
        Optional<Menu> findMenu = menuRepository.findById(id);
        if (findMenu.isPresent()) {
            return MenuResponse.fromEntity(findMenu.get());
        } else {
            throw new BadRequestException("Menu Not Found with ID : " + id);
        }
    }

    @Override
    public ApiResponse delete(Long id) {
        if (getById(id) != null) {
            menuRepository.deleteById(id);
            return new ApiResponse("Menu o'chirildi", true);
        }
        return new ApiResponse("Menu topilmadi", false);
    }

    @Override
    public List<MenuResponse> list() {
        List<Menu> menuList = menuRepository.findAll();
        return menuList.stream().map(MenuResponse::fromEntity).collect(Collectors.toList());
    }

    private Menu fillMenu(Menu menu, MenuRequest request) {
        menu.setTitle(request.getTitle());
        if (request.getParentId() != null) {
            menu.setParentMenu(entityManager.getReference(Menu.class, request.getParentId()));
        }
        return menu;
    }
}

package uz.napa.clinic.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uz.napa.clinic.exception.BadRequestException;

public class CommonUtils {
    public static void validatePageAndSize(int page, int size) {
        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page soni " + AppConstants.MAX_PAGE_SIZE + "dan oshishi mumkin emas !");
        }
        if (page < 0) {
            throw new BadRequestException("Page soni manfiy bolishi mumkin emas !");
        }
    }

    public static Pageable getPageable(int page, int size) {
        validatePageAndSize(page, size);
        return PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
    }
}

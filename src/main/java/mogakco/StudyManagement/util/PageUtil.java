package mogakco.StudyManagement.util;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import mogakco.StudyManagement.dto.SimplePageable;

@Component
public class PageUtil {

    public static SimplePageable createSimplePageable(Page<?> page) {
        SimplePageable simplePageable = new SimplePageable();
        simplePageable.setLast(page.isLast());
        simplePageable.setPage(page.getNumber());
        simplePageable.setSize(page.getSize());
        simplePageable.setTotalPages(page.getTotalPages());
        simplePageable.setTotalElements(page.getTotalElements());

        return simplePageable;
    }

}

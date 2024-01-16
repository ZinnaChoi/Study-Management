package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimplePageable {

    private boolean last;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;

}
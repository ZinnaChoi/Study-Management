package mogakco.StudyManagement.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberInfo {

    private String id;
    private String name;
    private List<String> scheduleNames;
    private String wakeupTime;
}

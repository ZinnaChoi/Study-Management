package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
import mogakco.StudyManagement.domain.AbsentSchedule;

@Getter
@Setter
public class AbsentDetail {

    private String memberName;

    private String description;

    private List<String> eventNameList = new ArrayList<>();

    public AbsentDetail(AbsentSchedule absentSchedule) {
        this.memberName = absentSchedule.getMember().getName();
        this.description = absentSchedule.getDescription();
    }

    public void addEventName(String name) {
        this.eventNameList.add(name);
    }

}

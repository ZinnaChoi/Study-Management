package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
import mogakco.StudyManagement.domain.AbsentSchedule;

@Getter
@Setter
public class AbsentCalendar {

    private String absentDate;

    private String eventName;

    private List<String> memberNameList = new ArrayList<>();

    public AbsentCalendar(AbsentSchedule absentSchedule) {
        this.absentDate = absentSchedule.getAbsentDate();
        this.eventName = absentSchedule.getSchedule().getEventName();
    }

    public void addMemberName(String name) {
        this.memberNameList.add(name);
    }

}

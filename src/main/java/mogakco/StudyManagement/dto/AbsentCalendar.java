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

    private String scheduleName;

    private List<String> memberNameList = new ArrayList<>();

    public AbsentCalendar(AbsentSchedule absentSchedule) {
        this.absentDate = absentSchedule.getAbsentDate();
        this.scheduleName = absentSchedule.getSchedule().getScheduleName();
    }

    public void addMemberName(String name) {
        this.memberNameList.add(name);
    }

}

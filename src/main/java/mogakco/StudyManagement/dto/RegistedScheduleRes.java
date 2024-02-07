package mogakco.StudyManagement.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistedScheduleRes extends CommonRes {

    private List<RegistedSchedule> registedSchedules;

    public RegistedScheduleRes(String systemId, Integer retCode, String retMsg,
            List<RegistedSchedule> registedSchedules) {
        super(systemId, retCode, retMsg);
        this.registedSchedules = registedSchedules;
    }
}

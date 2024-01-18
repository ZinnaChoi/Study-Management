package mogakco.StudyManagement.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AbsentCalendarRes extends DTOResCommon {

    private List<AbsentCalendar> content;

    public AbsentCalendarRes(String systemId, Integer retCode, String retMsg, List<AbsentCalendar> content) {
        super(systemId, retCode, retMsg);
        this.content = content;
    }

}

package mogakco.StudyManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleRes {

    private Long scheduleId;

    private String scheduleName;

    private String startTime;

    private String endTime;

}

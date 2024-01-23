package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleCreateReq {

    @Schema(example = "AM1")
    private String scheduleName;

    @Schema(example = "13:00")
    private String startTime;

    @Schema(example = "14:00")
    private String endTime;

    public ScheduleCreateReq() {
    }

    public ScheduleCreateReq(String scheduleName, String startTime, String endTime) {
        this.scheduleName = scheduleName;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

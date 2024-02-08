package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleReq {

    @Schema(example = "AM1")
    private String scheduleName;

    @Schema(example = "1300")
    @Pattern(regexp = "(0[0-9]|1[0-9]|2[0-3])([0-5][0-9])", message = "올바른 시, 분 형식을 입력하세요(HHMI)")
    private String startTime;

    @Schema(example = "1400")
    @Pattern(regexp = "(0[0-9]|1[0-9]|2[0-3])([0-5][0-9])", message = "올바른 시, 분 형식을 입력하세요(HHMI)")
    private String endTime;

    public ScheduleReq() {
    }

    public ScheduleReq(String scheduleName, String startTime, String endTime) {
        this.scheduleName = scheduleName;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

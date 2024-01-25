package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistedSchedule {
    @Schema(example = "1")
    private Long scheduleId;

    @Schema(example = "AM1")
    private String scheduleName;

    @Schema(example = "1300")
    private String startTime;

    @Schema(example = "1400")
    private String endTime;

}

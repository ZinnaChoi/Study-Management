package mogakco.StudyManagement.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyCreateReq extends DTOReqCommon {
    @NotBlank(message = "스터디 이름은 반드시 있어야 합니다")
    @Schema(example = "모각코 스터디")
    private String studyName;

    @Schema(example = "pwd")
    private byte[] studyLogo;

    private List<ScheduleCreateReq> schedules;

    public StudyCreateReq(String studyName, byte[] studyLogo,
            String dbUrl, String dbUser, String dbPassword, List<ScheduleCreateReq> schedules) {
        this.studyName = studyName;
        this.studyLogo = studyLogo;
        this.schedules = schedules;
    }
}

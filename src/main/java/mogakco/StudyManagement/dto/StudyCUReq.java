package mogakco.StudyManagement.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// Study Create, Update DTO
public class StudyCUReq extends DTOReqCommon {
    @NotBlank(message = "스터디 이름은 반드시 있어야 합니다")
    @Schema(example = "모각코 스터디")
    private String studyName;

    private List<ScheduleCUReq> schedules;

    public StudyCUReq(String studyName,
            String dbUrl, String dbUser, String dbPassword, List<ScheduleCUReq> schedules) {
        this.studyName = studyName;
        this.schedules = schedules;
    }
}

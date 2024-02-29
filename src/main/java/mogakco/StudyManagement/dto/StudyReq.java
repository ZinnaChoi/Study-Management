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
public class StudyReq {
    @NotBlank(message = "스터디 이름은 반드시 있어야 합니다")
    @Schema(example = "모각코 스터디")
    private String studyName;

    @Schema(example = "변경할 스터디 이름", description = "스터디 정보 수정 API에 사용")
    private String updateStudyName;

    @Schema(example = "현재 등록된 로고 사용 여부", description = "true: 현재 등록 로고 사용, false: 새 로고 또는 빈 로고 사용")
    private boolean useCurrentLogo;

    private List<ScheduleReq> schedules;

    public StudyReq(String studyName,
            String dbUrl, String dbUser, String dbPassword, List<ScheduleReq> schedules) {
        this.studyName = studyName;
        this.schedules = schedules;
    }
}

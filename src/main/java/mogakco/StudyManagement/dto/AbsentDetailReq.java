package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AbsentDetailReq extends DTOReqCommon {

    @Pattern(regexp = "^[0-9]{8}$", message = "absentDate는 8자리 숫자 형식 (yyyyMMdd)이어야 합니다")
    @Schema(example = "20240116")
    private String absentDate;

    public AbsentDetailReq(String sendDate, String systemId, String absentDate) {
        super(sendDate, systemId);
        this.absentDate = absentDate;
    }
}
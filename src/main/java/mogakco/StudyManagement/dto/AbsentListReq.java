package mogakco.StudyManagement.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AbsentListReq extends DTOReqCommon {

    @Pattern(regexp = "^[0-9]{6}$", message = "yearMonth는 6자리 숫자 형식 (yyyyMM)이어야 합니다")
    @Schema(example = "202401")
    private String yearMonth;

    @Schema(example = "[\"지나\",\"엠마\",\"딘\"]")
    private List<String> memberNameList;

}

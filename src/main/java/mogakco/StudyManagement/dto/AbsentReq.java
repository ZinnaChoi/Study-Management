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
public class AbsentReq {

    @Pattern(regexp = "^[0-9]{8}$", message = "부재 일자는 8자리 숫자 형식 (yyyyMMdd)이어야 합니다")
    @Schema(example = "20240116")
    private String absentDate;

    @Pattern(regexp = "^.{0,50}$", message = "부재 사유는 50자 이하여야 합니다")
    @Schema(example = "가족 여행")
    private String description;

    @Schema(example = "[\"PM1\",\"PM2\"]")
    private List<String> scheduleNameList;

    public AbsentReq(String absentDate, String description,
            List<String> scheduleNameList) {      
        this.absentDate = absentDate;
        this.description = description;
        this.scheduleNameList = scheduleNameList;
    }

}

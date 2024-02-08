package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberIdDuplRes extends CommonRes {

    @Schema(example = "false")
    private boolean isDuplicated;

    public MemberIdDuplRes(String systemId, int retCode, String retMsg) {
        super(systemId, retCode, retMsg);
    }
}

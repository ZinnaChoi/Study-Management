package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberIdDuplReq extends DTOReqCommon {
    @Schema(example = "admin")
    private String id;
}

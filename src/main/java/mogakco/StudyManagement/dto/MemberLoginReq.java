package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginReq {

    @Schema(example = "admin")
    private String username;

    @Schema(example = "password")
    private String password;
}

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
public class MemberLoginRes extends CommonRes {

    @Schema(example = "aaaaa.bbbbb.ccccc")
    private String token;

    @Schema(example = "USER")
    private String role;

    public MemberLoginRes(String systemId, int retCode, String retMsg, String token, String role) {
        super(systemId, retCode, retMsg);
        this.token = token;
        this.role = role;
    }

}

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
public class MemberLoginRes extends DTOResCommon {

    @Schema(example = "aaaaa.bbbbb.ccccc")
    private String token;

    public MemberLoginRes(String systemId, int retCode, String retMsg, String token) {
        super(systemId, retCode, retMsg);
        this.token = token;
    }

}

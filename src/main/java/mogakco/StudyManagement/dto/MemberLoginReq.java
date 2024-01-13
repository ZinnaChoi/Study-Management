package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberLoginReq extends DTOReqCommon {

    @Schema(example = "admin")
    private String id;

    @Schema(example = "password")
    private String password;

    public MemberLoginReq(String sendDate, String systemId, String username, String password) {
        super(sendDate, systemId);
        this.username = username;
        this.password = password;
    }
}

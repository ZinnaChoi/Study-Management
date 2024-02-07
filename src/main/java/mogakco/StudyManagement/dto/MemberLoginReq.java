package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberLoginReq {

    @Schema(example = "admin")
    private String id;

    @Schema(example = "password")
    private String password;

    public MemberLoginReq(String id, String password) {
        this.id = id;
        this.password = password;
    }
}

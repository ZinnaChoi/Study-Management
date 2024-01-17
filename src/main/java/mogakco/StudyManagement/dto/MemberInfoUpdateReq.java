package mogakco.StudyManagement.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mogakco.StudyManagement.enums.MemberUpdateType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoUpdateReq extends DTOReqCommon {

    @Schema(example = "NAME")
    private MemberUpdateType type;

    @Schema(example = "아무개")
    private String name;

    @Schema(example = "[\"AM1\", \"AM2\"]")
    private List<String> eventName;

    @Schema(example = "15:30")
    private String wakeupTime;

    @Schema(example = "password123!")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

}

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
public class MemberInfoUpdateReq {

    @Schema(example = "NAME")
    private MemberUpdateType type;

    @Schema(example = "아무개")
    private String name;

    @Schema(example = "010-1111-1111")
    @Pattern(regexp = "^[\\d]{2,3}-[\\d]{3,4}-[\\d]{4}+$", message = "전화번호 형식이 맞지 않습니다(***-****-****)")
    private String contact;

    @Schema(example = "[\"AM1\", \"AM2\"]")
    private List<String> scheduleName;

    @Schema(example = "1530")
    @Pattern(regexp = "(0[0-9]|1[0-9]|2[0-3])([0-5][0-9])", message = "올바른 시, 분 형식을 입력하세요(HHMI)")
    private String wakeupTime;

    @Schema(example = "password123!")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

}

package mogakco.StudyManagement.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberJoinReq {

    @Schema(example = "user1")
    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String id;

    @Schema(example = "password123!")
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @Schema(example = "HongGilDong")
    private String name;

    @Schema(example = "example123@mail.com")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Schema(example = "모각코 스터디")
    private String studyName;

    @Schema(example = "[\"AM1\", \"AM2\"]")
    private List<String> scheduleNames;

    @Schema(example = "1530")
    private String wakeupTime;
}

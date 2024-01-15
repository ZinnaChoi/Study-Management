package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberJoinReq extends DTOReqCommon {

    @Schema(example = "user1")
    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String id;

    @Schema(example = "pwd")
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @Schema(example = "HongGilDong")
    private String name;

    @Schema(example = "01011112222")
    private String contact;

    @Schema(example = "모각코 스터디")
    private String studyName;

    @Schema(example = "AM1")
    private String eventName;

    @Schema(example = "1530")
    private String wakeupTime;
}

package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import mogakco.StudyManagement.enums.MemberRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoRes extends CommonRes {

    @Schema(example = "user1")
    private String id;

    @Schema(example = "HongGilDong")
    private String name;

    @Schema(example = "email1@gmail.com")
    private String email;

    @Schema(example = "USER")
    private MemberRole role;

    @Schema(example = "모각코 스터디")
    private String studyName;

    @Schema(example = "AM1")
    private List<String> scheduleName;

    @Schema(example = "1530")
    private String wakeupTime;

    public MemberInfoRes(String systemId, int retCode, String retMsg, String id, String name, String email,
            MemberRole role, String studyName, List<String> scheduleName, String wakeupTime) {
        super(systemId, retCode, retMsg);
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.studyName = studyName;
        this.scheduleName = scheduleName;
        this.wakeupTime = wakeupTime;
    }

}

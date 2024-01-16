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
public class MemberInfoRes extends DTOResCommon {

    @Schema(example = "user1")
    private String id;

    @Schema(example = "HongGilDong")
    private String name;

    @Schema(example = "01011112222")
    private String contact;

    @Schema(example = "USER")
    private MemberRole role;

    @Schema(example = "모각코 스터디")
    private String studyName;

    @Schema(example = "AM1")
    private List<String> eventName;

    @Schema(example = "1530")
    private String wakeupTime;

    public MemberInfoRes(String systemId, int retCode, String retMsg, String id, String name, String contact,
            MemberRole role, String studyName, List<String> eventName, String wakeupTime) {
        super(systemId, retCode, retMsg);
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.role = role;
        this.studyName = studyName;
        this.eventName = eventName;
        this.wakeupTime = wakeupTime;
    }

}

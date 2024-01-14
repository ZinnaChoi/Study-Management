package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberJoinReq extends DTOReqCommon {

    @Schema(example = "user1")
    private String id;

    @Schema(example = "pwd")
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

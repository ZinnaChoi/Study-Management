package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeGetReq extends DTOReqCommon {

    @Schema(example = "1")
    private Long member;

    public NoticeGetReq(String sendDate, String systemId, Long member) {
        super(sendDate, systemId);
        this.member = member;
    }
}

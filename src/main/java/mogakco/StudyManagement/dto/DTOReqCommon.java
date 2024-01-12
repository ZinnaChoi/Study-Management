package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * sendDate 송신 시간(yyyyMMdd24HHmmssSSS)
 * systemId 송신 시스템 ID
 */
@Getter
@Setter
@NoArgsConstructor
public class DTOReqCommon {

    @Schema(name = "sendDate", example = "20240112113804899")
    private String sendDate;

    @Schema(name = "systemId", example = "STUDY_0001")
    private String systemId;

    public DTOReqCommon(String sendDate, String systemId) {
        this.sendDate = sendDate;
        this.systemId = systemId;
    }

}

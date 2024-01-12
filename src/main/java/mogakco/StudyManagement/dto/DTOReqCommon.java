package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/*
 * sendDate 송신 시간(yyyyMMdd24HHmmssSSS)
 * systemId 송신 시스템 ID
 */
@Getter
@Setter
public class DTOReqCommon {

    @Schema(name = "20240112113804899")
    private String sendDate;

    @Schema(name = "STUDY_0001")
    private String systemId;

}

package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * sendDate 송신 시간(yyyyMMdd24HHmmssSSS)
 * systemID 송신 시스템 ID
 */
@Getter
@Setter
public class DTOReqCommon {

    private String sendDate;
    private String systemID;

}

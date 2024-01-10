package mogakco.StudyManagement.domain;

import lombok.Getter;
import lombok.Setter;

/*
 * sendDate 송신 시간(yyyyMMdd24HHmmssSSS)
 * systemId 송신 시스템 ID
 */
@Getter
@Setter
public class DTOReqCommon {

    private String sendDate;
    private String systemId;

}

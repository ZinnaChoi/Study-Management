package mogakco.StudyManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mogakco.StudyManagement.util.DateUtil;

/*
 * sendDate 송신 시간(yyyyMMdd24HHmmssSSS)
 * systemId 송신 시스템 ID
 * retCode 결과 코드
 * retMsg 결과 메세지
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOResCommon {

    private String sendDate;
    private String systemId;
    private Integer retCode;
    private String retMsg;

    public DTOResCommon(String systemId, Integer retCode, String retMsg) {
        this.sendDate = DateUtil.getCurrentDateTime();
        this.setSystemId(systemId);
        this.retCode = retCode;
        this.retMsg = retMsg;
    }
}

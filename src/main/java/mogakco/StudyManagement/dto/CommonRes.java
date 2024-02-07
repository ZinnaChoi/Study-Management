package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * systemId 송신 시스템 ID
 * retCode 결과 코드
 * retMsg 결과 메세지
 */
@Getter
@Setter
@NoArgsConstructor
public class CommonRes {

    private String systemId;
    private Integer retCode;
    private String retMsg;

    public CommonRes(String systemId, Integer retCode, String retMsg) {
        this.setSystemId(systemId);
        this.retCode = retCode;
        this.retMsg = retMsg;
    }
}

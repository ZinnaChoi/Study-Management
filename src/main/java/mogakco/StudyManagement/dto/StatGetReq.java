package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatGetReq extends DTOReqCommon {

    public StatGetReq(String sendDate, String systemId) {
        super(sendDate, systemId);
    }

}

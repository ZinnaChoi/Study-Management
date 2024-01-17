package mogakco.StudyManagement.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AbsentListRes extends DTOResCommon {

    private List<AbsentList> content;

    public AbsentListRes(String systemId, Integer retCode, String retMsg, List<AbsentList> content) {
        super(systemId, retCode, retMsg);
        this.content = content;
    }

}

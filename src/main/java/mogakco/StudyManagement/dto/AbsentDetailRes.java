package mogakco.StudyManagement.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AbsentDetailRes extends CommonRes {

    private List<AbsentDetail> content;

    public AbsentDetailRes(String systemId, Integer retCode, String retMsg, List<AbsentDetail> content) {
        super(systemId, retCode, retMsg);
        this.content = content;
    }
}

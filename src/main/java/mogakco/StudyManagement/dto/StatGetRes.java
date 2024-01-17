package mogakco.StudyManagement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatGetRes extends DTOResCommon {

    private List<StatList> content;
    private SimplePageable pageable;

    public StatGetRes(String systemId, int retCode, String retMsg, List<StatList> content, SimplePageable pageable) {
        super(systemId, retCode, retMsg);
        this.content = content;
        this.pageable = pageable;
    }

}

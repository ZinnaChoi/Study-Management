package mogakco.StudyManagement.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberInfosRes extends CommonRes {

    private List<MemberInfo> content;
    private SimplePageable pageable;

    public MemberInfosRes(String systemId, Integer retCode, String retMsg, List<MemberInfo> content,
            SimplePageable pageable) {
        super(systemId, retCode, retMsg);
        this.content = content;
        this.pageable = pageable;
    }

}

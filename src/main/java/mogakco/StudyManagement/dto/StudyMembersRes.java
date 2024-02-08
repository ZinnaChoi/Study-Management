package mogakco.StudyManagement.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyMembersRes extends CommonRes {

    private List<MemberInfo> members;

    private SimplePageable pageable;

    public StudyMembersRes(String systemId, Integer retCode, String retMsg, List<MemberInfo> members,
            SimplePageable pageable) {
        super(systemId, retCode, retMsg);
        this.members = members;
        this.pageable = pageable;
    }
}

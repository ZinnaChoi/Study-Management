package mogakco.StudyManagement.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mogakco.StudyManagement.domain.Member;

@Getter
@Setter
@NoArgsConstructor
public class MemberListRes extends CommonRes {

    private List<Member> content;

    public MemberListRes(String systemId, Integer retCode, String retMsg, List<Member> content) {
        super(systemId, retCode, retMsg);
        this.content = content;
    }

}

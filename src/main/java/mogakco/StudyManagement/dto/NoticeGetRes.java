package mogakco.StudyManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mogakco.StudyManagement.domain.Notice;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeGetRes extends CommonRes {

    private Notice content;

    public NoticeGetRes(String systemId, int retCode, String retMsg, Notice content) {
        super(systemId, retCode, retMsg);
        this.content = content;

    }

}

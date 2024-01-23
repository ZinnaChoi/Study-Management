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
public class NoticeGetRes extends DTOResCommon {

    private List<NoticeList> content;

    public NoticeGetRes(String systemId, int retCode, String retMsg, List<NoticeList> content) {
        super(systemId, retCode, retMsg);
        this.content = content;

    }

}

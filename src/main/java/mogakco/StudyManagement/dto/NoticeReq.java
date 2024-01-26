package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeReq extends DTOReqCommon {

    private Boolean wakeup;

    private Boolean absent;

    private Boolean newPost;

    private Boolean linkShare;

    public NoticeReq(String sendDate, String systemId, Boolean wakeup, Boolean absent, Boolean newPost,
            Boolean linkShare) {
        super(sendDate, systemId);
        this.wakeup = wakeup;
        this.absent = absent;
        this.newPost = newPost;
        this.linkShare = linkShare;

    }
}
package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeReq {

    private Boolean wakeup;

    private Boolean absent;

    private Boolean newPost;

    private Boolean linkShare;

    public NoticeReq(Boolean wakeup, Boolean absent, Boolean newPost,
            Boolean linkShare) {
        this.wakeup = wakeup;
        this.absent = absent;
        this.newPost = newPost;
        this.linkShare = linkShare;

    }
}
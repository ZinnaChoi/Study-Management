package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;

import mogakco.StudyManagement.domain.Notice;

@Getter
@Setter
public class NoticeList {

    private String memberName;

    private Boolean wakeup;

    private Boolean absent;

    private Boolean newPost;

    private Boolean linkShare;

    public NoticeList(Notice notice) {

        this.memberName = notice.getMember().getName();
        this.wakeup = notice.getWakeup();
        this.absent = notice.getAbsent();
        this.newPost = notice.getNewPost();
        this.linkShare = notice.getLinkShare();
    }

}

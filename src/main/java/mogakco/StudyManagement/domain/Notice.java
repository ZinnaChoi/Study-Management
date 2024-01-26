package mogakco.StudyManagement.domain;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mogakco.StudyManagement.dto.NoticeReq;

import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "wakeup", nullable = false)
    @ColumnDefault("true")
    private Boolean wakeup;

    @Column(name = "absent", nullable = false)
    @ColumnDefault("true")
    private Boolean absent;

    @Column(name = "new_post", nullable = false)
    @ColumnDefault("true")
    private Boolean newPost;

    @Column(name = "link_share", nullable = false)
    @ColumnDefault("true")
    private Boolean linkShare;

    @Column(name = "last_share_date", nullable = false)
    private String lastShareDate;

    public Notice(Notice notice) {
        this.wakeup = notice.getWakeup();
        this.absent = notice.getAbsent();
        this.newPost = notice.getNewPost();
        this.linkShare = notice.getLinkShare();
    }

    public boolean isNoticeChanged(NoticeReq noticeReq) {
        return (!Objects.equals(wakeup, noticeReq.getWakeup()) || !Objects.equals(absent, noticeReq.getAbsent()) ||
                !Objects.equals(newPost, noticeReq.getNewPost())
                || !Objects.equals(linkShare, noticeReq.getLinkShare()));
    }

    public Notice updateNotice(NoticeReq noticeReq) {
        this.wakeup = noticeReq.getWakeup();
        this.absent = noticeReq.getAbsent();
        this.newPost = noticeReq.getNewPost();
        this.linkShare = noticeReq.getLinkShare();
        return this;
    }

}
package mogakco.StudyManagement.domain;

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
import mogakco.StudyManagement.dto.PostReq;
import mogakco.StudyManagement.util.DateUtil;

import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 60, nullable = false)
    private String title;

    @Column(length = 20000, nullable = false)
    private String content;

    @Builder.Default
    @Column(nullable = false)
    private Integer viewCnt = 0;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private String updatedAt;

    public void updatePost(PostReq postReq) {
        this.title = postReq.getTitle();
        this.content = postReq.getContent();
        this.updatedAt = DateUtil.getCurrentDateTime();
    }

    public boolean isPostChanged(PostReq postReq) {
        return (!Objects.equals(title, postReq.getTitle()) || !Objects.equals(content, postReq.getContent()));
    }

}

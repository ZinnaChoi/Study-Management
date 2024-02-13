package mogakco.StudyManagement.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import mogakco.StudyManagement.domain.Post;

@Getter
@Setter
public class PostDetail {

    private Long postId;

    private String memberName;

    private Integer likes;

    private String title;

    private String content;

    private Integer viewCnt;

    private String createdAt;

    private String updatedAt;

    private List<PostDetailComment> comments;

    public PostDetail(Post post, Integer likes, List<PostDetailComment> comments) {
        this.postId = post.getPostId();
        this.memberName = post.getMember().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.viewCnt = post.getViewCnt();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likes = likes;
        this.comments = comments;
    }
}

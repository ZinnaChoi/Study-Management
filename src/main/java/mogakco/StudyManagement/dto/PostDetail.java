package mogakco.StudyManagement.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import mogakco.StudyManagement.domain.Post;

@Getter
@Setter
public class PostDetail {
    private String memberName;

    private Integer likes;

    private String title;

    private String content;

    private String createdAt;

    private String updatedAt;

    private List<PostDetailComment> comments;

    public PostDetail(Post post, Integer likes, List<PostDetailComment> comments) {
        this.memberName = post.getMember().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likes = likes;
        this.comments = comments;
    }
}

package mogakco.StudyManagement.dto;

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

    public PostDetail(Post post, Integer likes) {

        this.memberName = post.getMember().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likes = likes;

    }
}

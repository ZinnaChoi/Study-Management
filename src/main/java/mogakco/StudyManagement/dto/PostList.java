package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;
import mogakco.StudyManagement.domain.Post;

@Getter
@Setter
public class PostList {

    private Integer likes;

    private String title;

    private String memberName;

    private Integer commentCnt;

    private String createdAt;

    private String updatedAt;

    public PostList(Post post, Integer likes, Integer commentCnt) {
        this.title = post.getTitle();
        this.memberName = post.getMember().getName();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likes = likes;
        this.commentCnt = commentCnt;
    }

}
package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;
import mogakco.StudyManagement.domain.Post;

@Getter
@Setter
public class PostList {

    private Integer likes;

    private Integer viewCnt;

    private String title;

    private String member;

    private Integer commentCnt;

    private String createdAt;

    private String updatedAt;

    public PostList(Post post) {
        this.title = post.getTitle();
        this.viewCnt = post.getViewCnt();
        this.member = post.getMember().getName();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likes = 0;
        this.commentCnt = 0;
    }

}
package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;
import mogakco.StudyManagement.domain.PostComment;

@Getter
@Setter
public class PostCommentReply {

    private String memberName;

    private String content;

    private String createdAt;

    private String updatedAt;

    public PostCommentReply(PostComment comment) {
        this.memberName = comment.getMember().getName();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }

}

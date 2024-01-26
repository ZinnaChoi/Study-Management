package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCommentReplyRes extends DTOResCommon {

    private List<PostCommentReply> postCommentReplies;

    public PostCommentReplyRes(String systemId, Integer retCode, String retMsg,
            List<PostCommentReply> postCommentReplies) {
        super(systemId, retCode, retMsg);
        this.postCommentReplies = postCommentReplies;
    }
}

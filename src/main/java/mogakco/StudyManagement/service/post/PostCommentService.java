package mogakco.StudyManagement.service.post;

import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.PostCommentReplyRes;
import mogakco.StudyManagement.dto.PostCommentReq;

public interface PostCommentService {

    CommonRes createPostComment(Long postId, PostCommentReq postCommentReq);

    CommonRes createPostCommentReply(Long postId, Long commentId, PostCommentReq postCommentReq);

    PostCommentReplyRes getCommentReply(Long postId, Long commentId);

    CommonRes updatePostComment(Long postId, Long commentId, PostCommentReq postCommentReq);

    CommonRes deletePostComment(Long postId, Long commentId);
}

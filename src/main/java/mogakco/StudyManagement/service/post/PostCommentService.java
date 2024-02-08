package mogakco.StudyManagement.service.post;

import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.PostCommentReplyRes;
import mogakco.StudyManagement.dto.PostCommentReq;
import mogakco.StudyManagement.service.common.LoggingService;

public interface PostCommentService {

    CommonRes createPostComment(Long postId, PostCommentReq postCommentReq, LoggingService lo);

    CommonRes createPostCommentReply(Long postId, Long commentId, PostCommentReq postCommentReq, LoggingService lo);

    PostCommentReplyRes getCommentReply(Long postId, Long commentId, LoggingService lo);

    CommonRes updatePostComment(Long postId, Long commentId, PostCommentReq postCommentReq, LoggingService lo);

    CommonRes deletePostComment(Long postId, Long commentId, LoggingService lo);
}

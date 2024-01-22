package mogakco.StudyManagement.service.post;

import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.PostCommentReq;
import mogakco.StudyManagement.service.common.LoggingService;

public interface PostCommentService {

    DTOResCommon createPostComment(Long postId, PostCommentReq postCommentReq, LoggingService lo);

    DTOResCommon createPostCommentReply(Long postId, Long commentId, PostCommentReq postCommentReq, LoggingService lo);

}
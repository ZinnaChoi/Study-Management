package mogakco.StudyManagement.service.post;

import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.service.common.LoggingService;

public interface PostLikeService {

    DTOResCommon createPostLike(Long postId, LoggingService lo);

    DTOResCommon deletePostLike(Long postId, LoggingService lo);

}

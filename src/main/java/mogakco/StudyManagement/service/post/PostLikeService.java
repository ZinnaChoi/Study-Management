package mogakco.StudyManagement.service.post;

import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.service.common.LoggingService;

public interface PostLikeService {

    CommonRes createPostLike(Long postId, LoggingService lo);

    CommonRes deletePostLike(Long postId, LoggingService lo);

}

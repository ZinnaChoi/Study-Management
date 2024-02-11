package mogakco.StudyManagement.service.post;

import mogakco.StudyManagement.dto.CommonRes;

public interface PostLikeService {

    CommonRes createPostLike(Long postId);

    CommonRes deletePostLike(Long postId);

}

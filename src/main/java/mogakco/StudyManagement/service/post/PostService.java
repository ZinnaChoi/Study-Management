package mogakco.StudyManagement.service.post;

import org.springframework.data.domain.Pageable;

import mogakco.StudyManagement.dto.PostReq;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.PostListReq;
import mogakco.StudyManagement.dto.PostListRes;
import mogakco.StudyManagement.service.common.LoggingService;

public interface PostService {
    void createPost(PostReq postCreateReq, LoggingService lo);

    PostListRes getPostList(PostListReq postListReq, LoggingService lo, Pageable pageable);

    DTOResCommon updatePost(Long postId, PostReq postUpdateReq, LoggingService lo);

    DTOResCommon deletePost(Long postId, LoggingService lo);
}

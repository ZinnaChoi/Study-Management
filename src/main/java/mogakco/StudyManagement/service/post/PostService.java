package mogakco.StudyManagement.service.post;

import org.springframework.data.domain.Pageable;

import mogakco.StudyManagement.dto.PostReq;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.PostDetailRes;
import mogakco.StudyManagement.dto.PostListReq;
import mogakco.StudyManagement.dto.PostListRes;
import mogakco.StudyManagement.service.common.LoggingService;

public interface PostService {
    void createPost(PostReq postCreateReq, LoggingService lo);

    PostListRes getPostList(PostListReq postListReq, LoggingService lo, Pageable pageable);

    PostDetailRes getPostDetail(Long postId, LoggingService lo);

    CommonRes updatePost(Long postId, PostReq postUpdateReq, LoggingService lo);

    CommonRes deletePost(Long postId, LoggingService lo);
}

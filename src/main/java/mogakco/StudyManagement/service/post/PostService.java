package mogakco.StudyManagement.service.post;

import org.springframework.data.domain.Pageable;

import mogakco.StudyManagement.dto.PostCreateReq;
import mogakco.StudyManagement.dto.PostListReq;
import mogakco.StudyManagement.dto.PostListRes;
import mogakco.StudyManagement.service.common.LoggingService;

public interface PostService {
    void createPost(PostCreateReq postCreateReq, LoggingService lo);

    PostListRes getPostList(PostListReq postListReq, LoggingService lo, Pageable pageable);
}

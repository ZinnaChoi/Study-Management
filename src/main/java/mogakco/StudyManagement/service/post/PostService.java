package mogakco.StudyManagement.service.post;

import org.springframework.data.domain.Pageable;

import mogakco.StudyManagement.dto.PostReq;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.PostDetailRes;
import mogakco.StudyManagement.dto.PostListReq;
import mogakco.StudyManagement.dto.PostListRes;

public interface PostService {
    void createPost(PostReq postCreateReq);

    PostListRes getPostList(PostListReq postListReq, Pageable pageable);

    PostDetailRes getPostDetail(Long postId);

    CommonRes updatePost(Long postId, PostReq postUpdateReq);

    CommonRes deletePost(Long postId);
}

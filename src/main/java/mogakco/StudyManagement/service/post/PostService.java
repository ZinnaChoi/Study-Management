package mogakco.StudyManagement.service.post;

import mogakco.StudyManagement.dto.PostCreateReq;
import mogakco.StudyManagement.service.common.LoggingService;

public interface PostService {
    void createPost(PostCreateReq postCreateReq, LoggingService lo);
}

package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.PostCreateReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostService;

@Tag(name = "게시판", description = "게시판 관련 API 분류")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1")
@RestController
public class PostController extends CommonController {

    private final PostService postService;

    public PostController(PostService postService, LoggingService lo) {
        super(lo);
        this.postService = postService;
    }

    @Operation(summary = "게시글 등록", description = "새 게시글 추가")
    @PostMapping("/posts")
    public DTOResCommon createPost(HttpServletRequest request, @RequestBody @Valid PostCreateReq postCreateReq) {

        DTOResCommon result = new DTOResCommon();
        try {
            startAPI(lo, postCreateReq);
            postService.createPost(postCreateReq, lo);
            result = setCommonResult(ErrorCode.OK, lo, DTOResCommon.class);
        } catch (Exception e) {
            result = setCommonResult(ErrorCode.INTERNAL_ERROR, lo, DTOResCommon.class);
        } finally {
            endAPI(request, ErrorCode.OK, lo, result);
        }
        return result;
    }
}

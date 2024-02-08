package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostLikeService;

@Tag(name = "게시글 좋아요", description = "게시글 좋아요 관련 API 분류")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1/posts")
@RestController
public class PostLikeController extends CommonController {

    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService, LoggingService lo) {
        super(lo);
        this.postLikeService = postLikeService;
    }

    @Operation(summary = "게시글 좋아요 등록", description = "게시글 좋아요 추가")
    @PostMapping("/{postId}/likes")
    public CommonRes createPostLike(HttpServletRequest request,
            @PathVariable(name = "postId", required = true) Long postId) {

        CommonRes result = new CommonRes();
        try {
            startAPI(lo, null);
            result = postLikeService.createPostLike(postId, lo);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "게시글 좋아요 취소", description = "게시글 좋아요 삭제")
    @DeleteMapping("/{postId}/likes")
    public CommonRes deletePostLike(HttpServletRequest request,
            @PathVariable(name = "postId", required = true) Long postId) {
        CommonRes result = new CommonRes();
        try {
            startAPI(lo, null);
            result = postLikeService.deletePostLike(postId, lo);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }

        return result;
    }

}

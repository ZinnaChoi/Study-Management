package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import jakarta.validation.Valid;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.PostCommentReplyRes;
import mogakco.StudyManagement.dto.PostCommentReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostCommentService;
import mogakco.StudyManagement.util.DateUtil;

@Tag(name = "게시판 댓글", description = "게시판 댓글 관련 API 분류")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1/posts")
@RestController
public class PostCommentController extends CommonController {

    private final PostCommentService postCommentService;

    public PostCommentController(PostCommentService postCommentService, LoggingService lo) {
        super(lo);
        this.postCommentService = postCommentService;
    }

    @Operation(summary = "게시판 댓글 등록", description = "새 댓글 추가")
    @PostMapping("/{postId}/comments")
    public DTOResCommon createPostComment(HttpServletRequest request,
            @PathVariable(name = "postId", required = true) Long postId,
            @RequestBody @Valid PostCommentReq postCommentReq) {

        DTOResCommon result = new DTOResCommon();
        try {
            startAPI(lo, postCommentReq);
            result = postCommentService.createPostComment(postId, postCommentReq, lo);
            result.setSystemId(systemId);
            result.setSendDate(DateUtil.getCurrentDateTime());
        } catch (Exception e) {
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "게시판 답글 등록", description = "새 답글 추가")
    @PostMapping("/{postId}/comments/{commentId}/replies")
    public DTOResCommon createPostCommentReply(HttpServletRequest request,
            @PathVariable(name = "postId", required = true) Long postId,
            @PathVariable(name = "commentId", required = true) Long commentId,
            @RequestBody @Valid PostCommentReq postCommentReq) {

        DTOResCommon result = new DTOResCommon();
        try {
            startAPI(lo, postCommentReq);
            result = postCommentService.createPostCommentReply(postId, commentId, postCommentReq, lo);
            result.setSystemId(systemId);
            result.setSendDate(DateUtil.getCurrentDateTime());
        } catch (Exception e) {
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "게시판 답글 조회", description = "답글 조회")
    @GetMapping("/{postId}/comments/{commentId}/replies")
    public PostCommentReplyRes getCommentReply(HttpServletRequest request,
            @PathVariable(name = "postId", required = true) Long postId,
            @PathVariable(name = "commentId", required = true) Long commentId) {

        PostCommentReplyRes result = new PostCommentReplyRes();
        try {
            startAPI(lo, null);
            result = postCommentService.getCommentReply(postId, commentId, lo);
            result.setSendDate(DateUtil.getCurrentDateTime());
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new PostCommentReplyRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "게시판 댓글 및 답글 수정", description = "게시판의 댓글 및 답글 수정")
    @PatchMapping("/{postId}/comments/{commentId}")
    public DTOResCommon updateComment(HttpServletRequest request,
            @PathVariable(name = "postId", required = true) Long postId,
            @PathVariable(name = "commentId", required = true) Long commentId,
            @RequestBody @Valid PostCommentReq postCommentReq) {
        DTOResCommon result = new DTOResCommon();

        try {
            startAPI(lo, postCommentReq);
            result = postCommentService.updatePostComment(postId, commentId, postCommentReq, lo);
            result.setSendDate(DateUtil.getCurrentDateTime());
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

}

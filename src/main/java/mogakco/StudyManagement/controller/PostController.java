package mogakco.StudyManagement.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import mogakco.StudyManagement.dto.PostListReq;
import mogakco.StudyManagement.dto.PostListRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostService;
import mogakco.StudyManagement.util.DateUtil;

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
            result = new DTOResCommon(systemId, ErrorCode.OK.getCode(),
                    ErrorCode.OK.getMessage());
        } catch (Exception e) {
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, ErrorCode.OK, lo, result);
        }
        return result;
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 검색 및 페이지 별 조회")
    @GetMapping("/posts")
    public PostListRes getPostList(
            HttpServletRequest request, @ModelAttribute @Valid PostListReq postListReq,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PostListRes result = new PostListRes();
        try {
            startAPI(lo, postListReq);
            result = postService.getPostList(postListReq, lo, pageable);
            result.setSendDate(DateUtil.getCurrentDateTime());
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new PostListRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null);
        } finally {
            endAPI(request, ErrorCode.OK, lo, result);
        }

        return result;

    }

}

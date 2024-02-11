package mogakco.StudyManagement.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.PostDetailRes;
import mogakco.StudyManagement.dto.PostListReq;
import mogakco.StudyManagement.dto.PostListRes;
import mogakco.StudyManagement.dto.PostReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.post.PostService;

@Tag(name = "게시판", description = "게시판 관련 API 분류")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1")
@RestController
public class PostController extends CommonController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "게시글 등록", description = "새 게시글 추가")
    @PostMapping("/posts")
    public CommonRes createPost(HttpServletRequest request, @RequestBody @Valid PostReq postCreateReq) {
        CommonRes result = new CommonRes();
        try {
            postService.createPost(postCreateReq);
            result = new CommonRes(systemId, ErrorCode.CREATED.getCode(),
                    ErrorCode.CREATED.getMessage("게시글"));
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
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
            result = postService.getPostList(postListReq, pageable);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new PostListRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null);
        }
        return result;
    }

    @Operation(summary = "게시글 상세 정보 조회", description = "특정 게시글의 상세 정보 조회")
    @GetMapping("/posts/{postId}")
    public PostDetailRes getPostDetail(HttpServletRequest request,
            @PathVariable(name = "postId", required = true) Long postId) {
        PostDetailRes result = new PostDetailRes();

        try {
            result = postService.getPostDetail(postId);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new PostDetailRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        }
        return result;

    }

    @Operation(summary = "게시글 수정", description = "특정 게시글 수정. 게시글 작성자만 수정 가능")
    @PatchMapping("/posts/{postId}")
    public CommonRes updatePost(HttpServletRequest request,
            @PathVariable(name = "postId", required = true) Long postId,
            @RequestBody @Valid PostReq postUpdateReq) {
        CommonRes result = new CommonRes();
        try {
            result = postService.updatePost(postId, postUpdateReq);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;

    }

    @Operation(summary = "게시글 삭제", description = "특정 게시글 삭제. 게시글 작성자만 삭제 가능")
    @DeleteMapping("/posts/{postId}")
    public CommonRes deletePost(HttpServletRequest request,
            @PathVariable(name = "postId", required = true) Long postId) {
        CommonRes result = new CommonRes();
        try {
            result = postService.deletePost(postId);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;
    }

}

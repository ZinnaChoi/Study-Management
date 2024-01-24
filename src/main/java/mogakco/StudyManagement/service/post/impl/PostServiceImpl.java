package mogakco.StudyManagement.service.post.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.dto.PostReq;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.PostDetail;
import mogakco.StudyManagement.dto.PostDetailRes;
import mogakco.StudyManagement.dto.PostList;
import mogakco.StudyManagement.dto.PostListReq;
import mogakco.StudyManagement.dto.PostListRes;
import mogakco.StudyManagement.dto.SimplePageable;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.enums.PostSearchType;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.exception.UnauthorizedAccessException;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.PostCommentRepository;
import mogakco.StudyManagement.repository.PostLikeRepository;
import mogakco.StudyManagement.repository.PostRepository;
import mogakco.StudyManagement.repository.PostSpecification;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.ExceptionUtil;
import mogakco.StudyManagement.util.PageUtil;
import mogakco.StudyManagement.util.SecurityUtil;

@Service
public class PostServiceImpl implements PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostLikeRepository postLikeRepository;

    public PostServiceImpl(MemberRepository memberRepository, PostRepository postRepository,
            PostCommentRepository postCommentRepository, PostLikeRepository postLikeRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.postLikeRepository = postLikeRepository;
    }

    @Override
    @Transactional
    public void createPost(PostReq postCreateReq, LoggingService lo) {
        lo.setDBStart();
        Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
        lo.setDBEnd();

        Post post = Post.builder().member(member).title(postCreateReq.getTitle()).content(postCreateReq.getContent())
                .createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime())
                .build();

        lo.setDBStart();
        postRepository.save(post);
        lo.setDBEnd();
    }

    @Override
    public PostListRes getPostList(PostListReq postListReq, LoggingService lo, Pageable pageable) {

        Page<Post> posts;
        String searchKeyWord = postListReq.getSearchKeyWord().trim();
        Specification<Post> spec;

        lo.setDBStart();
        if (postListReq.getSearchType() == PostSearchType.TITLE) {
            spec = PostSpecification.withTitleContaining(searchKeyWord);
        } else {
            lo.setDBStart();
            List<Member> members = memberRepository.findByNameContaining(searchKeyWord);
            spec = PostSpecification.withMemberIn(members);
            lo.setDBEnd();
        }
        posts = postRepository.findAll(spec, pageable);
        lo.setDBEnd();

        List<PostList> postLists = posts.getContent().stream()
                .map(post -> {
                    lo.setDBStart();
                    Integer commentCount = postCommentRepository.countByPostPostId(post.getPostId());
                    Integer likeCount = postLikeRepository.countByPostPostId(post.getPostId());
                    lo.setDBEnd();
                    return new PostList(post, likeCount, commentCount);
                })
                .collect(Collectors.toList());
        SimplePageable simplePageable = PageUtil.createSimplePageable(posts);

        return new PostListRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), postLists, simplePageable);
    }

    @Override
    public PostDetailRes getPostDetail(Long postId, LoggingService lo) {

        try {
            Specification<Post> spec = PostSpecification.withPostId(postId);
            lo.setDBStart();
            Post post = postRepository.findOne(spec)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글")));
            Integer likeCount = postLikeRepository.countByPostPostId(postId);
            lo.setDBEnd();

            return new PostDetailRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(),
                    new PostDetail(post, likeCount));
        } catch (NotFoundException e) {
            DTOResCommon res = ExceptionUtil.handleException(e);
            return new PostDetailRes(res.getSystemId(), res.getRetCode(), res.getRetMsg(), null);
        }
    }

    @Override
    @Transactional
    public DTOResCommon updatePost(Long postId, PostReq postUpdateReq, LoggingService lo) {
        try {
            DTOResCommon result = new DTOResCommon();
            Specification<Post> spec = PostSpecification.withPostId(postId);
            lo.setDBStart();
            Post post = postRepository.findOne(spec)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글")));

            Member loginMember = memberRepository.findById(SecurityUtil.getLoginUserId());
            lo.setDBEnd();
            if (!post.getMember().equals(loginMember)) {
                throw new UnauthorizedAccessException(
                        ErrorCode.BAD_REQUEST.getMessage("작성하지 않은 게시글은 수정할 수 없습니다."));
            }
            if (post.isPostChanged(postUpdateReq)) {
                post.updateTitle(postUpdateReq.getTitle());
                post.updateContent(postUpdateReq.getContent());
                post.updateUpdatedAt(DateUtil.getCurrentDateTime());
                lo.setDBStart();
                postRepository.save(post);
                lo.setDBEnd();
                result.setRetCode(ErrorCode.OK.getCode());
                result.setRetMsg(ErrorCode.OK.getMessage("게시글"));
            } else {
                result.setRetCode(ErrorCode.UNCHANGED.getCode());
                result.setRetMsg(ErrorCode.UNCHANGED.getMessage("게시글"));
            }
            return result;

        } catch (NotFoundException | UnauthorizedAccessException e) {
            return ExceptionUtil.handleException(e);
        }
    }

    @Override
    @Transactional
    public DTOResCommon deletePost(Long postId, LoggingService lo) {
        try {
            Specification<Post> spec = PostSpecification.withPostId(postId);
            lo.setDBStart();
            Post post = postRepository.findOne(spec)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글")));

            Member loginMember = memberRepository.findById(SecurityUtil.getLoginUserId());
            lo.setDBEnd();

            if (!post.getMember().equals(loginMember)) {
                throw new UnauthorizedAccessException(
                        ErrorCode.BAD_REQUEST.getMessage("작성하지 않은 게시글은 삭제할 수 없습니다."));
            }
            lo.setDBStart();
            postRepository.delete(post);
            lo.setDBEnd();

            return new DTOResCommon(null, ErrorCode.DELETED.getCode(),
                    ErrorCode.DELETED.getMessage("게시글"));
        } catch (NotFoundException | UnauthorizedAccessException e) {
            return ExceptionUtil.handleException(e);
        }
    }
}

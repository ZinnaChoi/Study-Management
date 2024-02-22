package mogakco.StudyManagement.service.post.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.domain.PostComment;
import mogakco.StudyManagement.dto.PostReq;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.PostDetail;
import mogakco.StudyManagement.dto.PostDetailComment;
import mogakco.StudyManagement.dto.PostDetailRes;
import mogakco.StudyManagement.dto.PostList;
import mogakco.StudyManagement.dto.PostListReq;
import mogakco.StudyManagement.dto.PostListRes;
import mogakco.StudyManagement.enums.ErrorCode;

import mogakco.StudyManagement.enums.MessageType;
import mogakco.StudyManagement.enums.PostSearchType;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.exception.UnauthorizedAccessException;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.PostCommentRepository;
import mogakco.StudyManagement.repository.PostLikeRepository;
import mogakco.StudyManagement.repository.PostRepository;
import mogakco.StudyManagement.repository.spec.PostSpecification;
import mogakco.StudyManagement.service.notice.NoticeService;
import mogakco.StudyManagement.service.post.PostCommonService;
import mogakco.StudyManagement.service.post.PostService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.ExceptionUtil;
import mogakco.StudyManagement.util.PageUtil;

@Service
public class PostServiceImpl extends PostCommonService implements PostService {

    private final PostCommentRepository postCommentRepository;
    private final PostLikeRepository postLikeRepository;
    private final StringRedisTemplate redisTemplate;
    private final NoticeService noticeService;

    public PostServiceImpl(MemberRepository memberRepository, PostRepository postRepository,
            PostCommentRepository postCommentRepository, PostLikeRepository postLikeRepository,
            StringRedisTemplate redisTemplate, NoticeService noticeService) {
        super(memberRepository, postRepository);
        this.postCommentRepository = postCommentRepository;
        this.postLikeRepository = postLikeRepository;
        this.redisTemplate = redisTemplate;
        this.noticeService = noticeService;
    }

    @Override
    @Transactional
    public void createPost(PostReq postCreateReq) {
        Member member = getLoginMember();
        Post post = Post.builder().member(member).title(postCreateReq.getTitle()).content(postCreateReq.getContent())
                .createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime())
                .build();

        postRepository.save(post);
        noticeService.createSpecificNotice(member, MessageType.NEW_POST);
    }

    @Override
    public PostListRes getPostList(PostListReq postListReq, Pageable pageable) {

        String searchKeyWord = postListReq.getSearchKeyWord().trim();
        Specification<Post> spec;
        Page<Post> posts;

        if (searchKeyWord.length() == 0) {
            posts = postRepository.findAll(pageable);
        } else {
            if (postListReq.getSearchType() == PostSearchType.TITLE) {
                spec = PostSpecification.withTitleContaining(searchKeyWord);
            } else {
                List<Member> members = memberRepository.findByNameContaining(searchKeyWord);
                spec = PostSpecification.withMemberIn(members);
            }
            posts = postRepository.findAll(spec, pageable);
        }

        List<PostList> postLists = posts.getContent().stream()
                .map(post -> {
                    Integer commentCount = postCommentRepository.countByPostPostId(post.getPostId());
                    Integer likeCount = postLikeRepository.countByPostPostId(post.getPostId());
                    return new PostList(post, likeCount, commentCount);
                })
                .collect(Collectors.toList());
        return new PostListRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), postLists,
                PageUtil.createSimplePageable(posts));
    }

    @Override
    public PostDetailRes getPostDetail(Long postId) {
        try {
            Post post = getPostById(postId);
            incrementViewCntIfNotViewed(post);
            Integer likeCount = postLikeRepository.countByPostPostId(postId);
            List<PostComment> commentEntities = postCommentRepository.findByPostPostIdAndParentCommentIsNull(postId);
            List<Object[]> replyCntByPostId = postCommentRepository.countRepliesByPostId(postId);

            Map<Long, Integer> replyCountMap = replyCntByPostId.stream()
                    .collect(Collectors.toMap(
                            result -> ((Number) result[0]).longValue(),
                            result -> ((Number) result[1]).intValue()));

            List<PostDetailComment> postCommentList = commentEntities.stream().map(entity -> {
                PostDetailComment dto = new PostDetailComment();
                dto.setCommentId(entity.getCommentId());
                dto.setMemberName(entity.getMember() == null ? "[Unknown]" : entity.getMember().getName());
                dto.setContent(entity.getContent());
                dto.setCreatedAt(entity.getCreatedAt());
                dto.setUpdatedAt(entity.getUpdatedAt());
                dto.setReplyCnt(replyCountMap.getOrDefault(entity.getCommentId(), 0));
                return dto;
            }).collect(Collectors.toList());

            return new PostDetailRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(),
                    new PostDetail(post, likeCount, postCommentList));
        } catch (NotFoundException e) {
            CommonRes res = ExceptionUtil.handleException(e);
            return new PostDetailRes(res.getSystemId(), res.getRetCode(), res.getRetMsg(), null);
        }
    }

    @Override
    @Transactional
    public CommonRes updatePost(Long postId, PostReq postUpdateReq) {
        try {
            CommonRes result = new CommonRes();

            Post post = getPostById(postId);
            Member loginMember = getLoginMember();

            if (!post.getMember().equals(loginMember)) {
                throw new UnauthorizedAccessException(
                        ErrorCode.UNAUTHORIZED.getMessage("작성하지 않은 게시글은 수정할 수 없습니다."));
            }
            if (post.isPostChanged(postUpdateReq)) {
                post.updatePost(postUpdateReq);
                postRepository.save(post);
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
    public CommonRes deletePost(Long postId) {
        try {
            Post post = getPostById(postId);
            Member loginMember = getLoginMember();

            if (!post.getMember().equals(loginMember)) {
                throw new UnauthorizedAccessException(
                        ErrorCode.UNAUTHORIZED.getMessage("작성하지 않은 게시글은 삭제할 수 없습니다."));
            }
            postRepository.delete(post);

            return new CommonRes(null, ErrorCode.DELETED.getCode(),
                    ErrorCode.DELETED.getMessage("게시글"));
        } catch (NotFoundException | UnauthorizedAccessException e) {
            return ExceptionUtil.handleException(e);
        }
    }

    public void incrementViewCntIfNotViewed(Post post) {
        Long loginMemberId = getLoginMember().getMemberId();

        String key = "post:viewCount:" + post.getPostId() + ":" + loginMemberId;
        Boolean alreadyViewed = redisTemplate.opsForSet().isMember(key, String.valueOf(loginMemberId));

        if (Boolean.FALSE.equals(alreadyViewed)) {
            redisTemplate.opsForSet().add(key, String.valueOf(loginMemberId));
            postRepository.incrementViewCount(post.getPostId());
        }
    }

}

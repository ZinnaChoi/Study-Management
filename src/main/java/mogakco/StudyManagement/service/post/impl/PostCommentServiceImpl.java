package mogakco.StudyManagement.service.post.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.domain.PostComment;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.PostCommentReply;
import mogakco.StudyManagement.dto.PostCommentReplyRes;
import mogakco.StudyManagement.dto.PostCommentReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.InvalidRequestException;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.exception.UnauthorizedAccessException;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.PostCommentRepository;
import mogakco.StudyManagement.repository.PostRepository;
import mogakco.StudyManagement.repository.spec.PostCommentSpecification;
import mogakco.StudyManagement.repository.spec.PostSpecification;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostCommentService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.ExceptionUtil;
import mogakco.StudyManagement.util.SecurityUtil;

@Service
public class PostCommentServiceImpl implements PostCommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    public PostCommentServiceImpl(MemberRepository memberRepository, PostRepository postRepository,
            PostCommentRepository postCommentRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
    }

    @Override
    @Transactional
    public DTOResCommon createPostComment(Long postId, PostCommentReq postCommentReq, LoggingService lo) {
        try {
            lo.setDBStart();
            Member member = getLoginMember();
            Post post = getPostById(postId);
            lo.setDBEnd();

            PostComment comment = PostComment.builder().member(member).parentComment(null).post(post)
                    .content(postCommentReq.getContent()).createdAt(DateUtil.getCurrentDateTime())
                    .updatedAt(DateUtil.getCurrentDateTime()).build();

            lo.setDBStart();
            postCommentRepository.save(comment);
            lo.setDBEnd();

            return new DTOResCommon(null, ErrorCode.CREATED.getCode(), ErrorCode.CREATED.getMessage("게시판 댓글"));

        } catch (NotFoundException e) {
            return ExceptionUtil.handleException(e);
        }
    }

    @Override
    @Transactional
    public DTOResCommon createPostCommentReply(Long postId, Long commentId, PostCommentReq postCommentReq,
            LoggingService lo) {
        try {
            lo.setDBStart();
            Member member = getLoginMember();
            Post post = getPostById(postId);
            PostComment postComment = getCommentByPostIdAndCommentId(postId, commentId);
            lo.setDBEnd();

            if (postComment.getParentComment() != null) {
                throw new InvalidRequestException(ErrorCode.BAD_REQUEST.getMessage("답글에는 답글을 생성할 수 없습니다"));
            }

            PostComment reply = PostComment.builder().member(member).parentComment(postComment).post(post)
                    .content(postCommentReq.getContent()).createdAt(DateUtil.getCurrentDateTime())
                    .updatedAt(DateUtil.getCurrentDateTime()).build();

            lo.setDBStart();
            postCommentRepository.save(reply);
            lo.setDBEnd();

            return new DTOResCommon(null, ErrorCode.CREATED.getCode(), ErrorCode.CREATED.getMessage("게시판 답글"));

        } catch (NotFoundException | InvalidRequestException e) {
            return ExceptionUtil.handleException(e);
        }
    }

    @Override
    public PostCommentReplyRes getCommentReply(Long postId, Long commentId, LoggingService lo) {
        try {
            Specification<PostComment> replySpec = PostCommentSpecification.withParentCommentId(commentId);
            lo.setDBStart();
            if (postRepository.countByPostId(postId) == 0)
                throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글"));
            PostComment postComment = getCommentByPostIdAndCommentId(postId, commentId);
            lo.setDBEnd();

            if (postComment.getParentComment() != null)
                throw new InvalidRequestException(ErrorCode.BAD_REQUEST.getMessage("답글에 대한 답글 조회는 지원하지 않습니다."));

            lo.setDBStart();
            List<PostComment> replies = postCommentRepository.findAll(replySpec);
            lo.setDBEnd();

            List<PostCommentReply> postCommentReplies = replies.stream()
                    .map(PostCommentReply::new)
                    .collect(Collectors.toList());

            return new PostCommentReplyRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(),
                    postCommentReplies);

        } catch (NotFoundException | InvalidRequestException e) {
            DTOResCommon res = ExceptionUtil.handleException(e);
            return new PostCommentReplyRes(res.getSystemId(), res.getRetCode(), res.getRetMsg(), null);
        }
    }

    @Override
    @Transactional
    public DTOResCommon updatePostComment(Long postId, Long commentId, PostCommentReq postCommentReq,
            LoggingService lo) {
        DTOResCommon result = new DTOResCommon();
        try {
            lo.setDBStart();
            Member loginMember = getLoginMember();
            if (postRepository.countByPostId(postId) == 0)
                throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글"));
            PostComment postComment = getCommentByPostIdAndCommentId(postId, commentId);
            lo.setDBEnd();

            if (!postComment.getMember().equals(loginMember)) {
                throw new UnauthorizedAccessException(
                        ErrorCode.BAD_REQUEST.getMessage("작성하지 않은 댓글은 수정할 수 없습니다."));
            }
            if (postComment.isPostCommentChanged(postCommentReq)) {
                postComment.updatePostComment(postCommentReq);
                lo.setDBStart();
                postCommentRepository.save(postComment);
                lo.setDBEnd();
                result.setRetCode(ErrorCode.OK.getCode());
                result.setRetMsg(ErrorCode.OK.getMessage("게시판 댓글"));
            } else {
                result.setRetCode(ErrorCode.UNCHANGED.getCode());
                result.setRetMsg(ErrorCode.UNCHANGED.getMessage("게시판 댓글"));
            }
            return result;

        } catch (NotFoundException | UnauthorizedAccessException e) {
            return ExceptionUtil.handleException(e);
        }

    }

    @Override
    @Transactional
    public DTOResCommon deletePostComment(Long postId, Long commentId, LoggingService lo) {
        try {
            lo.setDBStart();
            Member loginMember = getLoginMember();
            if (postRepository.countByPostId(postId) == 0)
                throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글"));
            PostComment postComment = getCommentByPostIdAndCommentId(postId, commentId);
            lo.setDBEnd();

            if (!postComment.getMember().equals(loginMember)) {
                throw new UnauthorizedAccessException(
                        ErrorCode.BAD_REQUEST.getMessage("작성하지 않은 댓글(답글)은 삭제할 수 없습니다."));
            }
            lo.setDBStart();
            postCommentRepository.delete(postComment);
            lo.setDBEnd();

            return new DTOResCommon(null, ErrorCode.DELETED.getCode(),
                    ErrorCode.DELETED.getMessage("게시판 댓글(답글)"));

        } catch (NotFoundException | UnauthorizedAccessException e) {
            return ExceptionUtil.handleException(e);
        }

    }

    private Member getLoginMember() {
        return memberRepository.findById(SecurityUtil.getLoginUserId());
    }

    private Post getPostById(Long postId) {
        Specification<Post> spec = PostSpecification.withPostId(postId);
        return postRepository.findOne(spec)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글")));
    }

    private PostComment getCommentByPostIdAndCommentId(Long postId, Long commentId) {
        Specification<PostComment> spec = PostCommentSpecification.withPostIdAndCommentId(postId, commentId);
        return postCommentRepository.findOne(spec)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시판 댓글")));
    }

}

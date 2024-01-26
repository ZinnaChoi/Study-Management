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
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.PostCommentRepository;
import mogakco.StudyManagement.repository.PostCommentSpecification;
import mogakco.StudyManagement.repository.PostRepository;
import mogakco.StudyManagement.repository.PostSpecification;
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
            Specification<Post> spec = PostSpecification.withPostId(postId);

            lo.setDBStart();
            Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
            Post post = postRepository.findOne(spec)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글")));
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
            Specification<Post> postSpec = PostSpecification.withPostId(postId);
            Specification<PostComment> commentSpec = PostCommentSpecification.withCommentId(commentId);

            lo.setDBStart();
            Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
            Post post = postRepository.findOne(postSpec)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글")));
            PostComment postComment = postCommentRepository.findOne(commentSpec)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시판 댓글")));
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
            Specification<Post> postSpec = PostSpecification.withPostId(postId);
            Specification<PostComment> commentSpec = PostCommentSpecification.withCommentId(commentId);
            Specification<PostComment> replySpec = PostCommentSpecification.withParentCommentId(commentId);

            lo.setDBStart();
            Post post = postRepository.findOne(postSpec)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글")));
            PostComment parentComment = postCommentRepository.findOne(commentSpec)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시판 댓글")));
            lo.setDBEnd();

            if (parentComment.getParentComment() != null) {
                throw new InvalidRequestException(ErrorCode.BAD_REQUEST.getMessage("답글에 대한 답글 조회는 지원하지 않습니다."));
            }

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
}

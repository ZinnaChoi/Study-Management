package mogakco.StudyManagement.service.post.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.domain.PostComment;
import mogakco.StudyManagement.dto.CommonRes;
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
import mogakco.StudyManagement.service.post.PostCommentService;
import mogakco.StudyManagement.service.post.PostCommonService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.ExceptionUtil;

@Service
public class PostCommentServiceImpl extends PostCommonService implements PostCommentService {

    private final PostCommentRepository postCommentRepository;

    public PostCommentServiceImpl(MemberRepository memberRepository, PostRepository postRepository,
            PostCommentRepository postCommentRepository) {
        super(memberRepository, postRepository);
        this.postCommentRepository = postCommentRepository;
    }

    @Override
    @Transactional
    public CommonRes createPostComment(Long postId, PostCommentReq postCommentReq) {
        try {
            Member member = getLoginMember();
            Post post = getPostById(postId);

            PostComment comment = PostComment.builder().member(member).parentComment(null).post(post)
                    .content(postCommentReq.getContent()).createdAt(DateUtil.getCurrentDateTime())
                    .updatedAt(DateUtil.getCurrentDateTime()).build();
            postCommentRepository.save(comment);

            return new CommonRes(null, ErrorCode.CREATED.getCode(), ErrorCode.CREATED.getMessage("게시판 댓글"));

        } catch (NotFoundException e) {
            return ExceptionUtil.handleException(e);
        }
    }

    @Override
    @Transactional
    public CommonRes createPostCommentReply(Long postId, Long commentId, PostCommentReq postCommentReq) {
        try {
            Member member = getLoginMember();
            Post post = getPostById(postId);
            PostComment postComment = getCommentByPostIdAndCommentId(postId, commentId);

            if (postComment.getParentComment() != null) {
                throw new InvalidRequestException(ErrorCode.BAD_REQUEST.getMessage("답글에는 답글을 생성할 수 없습니다"));
            }
            PostComment reply = PostComment.builder().member(member).parentComment(postComment).post(post)
                    .content(postCommentReq.getContent()).createdAt(DateUtil.getCurrentDateTime())
                    .updatedAt(DateUtil.getCurrentDateTime()).build();

            postCommentRepository.save(reply);
            return new CommonRes(null, ErrorCode.CREATED.getCode(), ErrorCode.CREATED.getMessage("게시판 답글"));

        } catch (NotFoundException | InvalidRequestException e) {
            return ExceptionUtil.handleException(e);
        }
    }

    @Override
    public PostCommentReplyRes getCommentReply(Long postId, Long commentId) {
        try {
            isPostExistById(postId);
            PostComment postComment = getCommentByPostIdAndCommentId(postId, commentId);

            if (postComment.getParentComment() != null)
                throw new InvalidRequestException(ErrorCode.BAD_REQUEST.getMessage("답글에 대한 답글 조회는 지원하지 않습니다."));

            List<PostComment> replies = postCommentRepository.findByParentCommentCommentId(commentId);
            List<PostCommentReply> postCommentReplies = replies.stream()
                    .map(PostCommentReply::new)
                    .collect(Collectors.toList());

            return new PostCommentReplyRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(),
                    postCommentReplies);

        } catch (NotFoundException | InvalidRequestException e) {
            CommonRes res = ExceptionUtil.handleException(e);
            return new PostCommentReplyRes(res.getSystemId(), res.getRetCode(), res.getRetMsg(), null);
        }
    }

    @Override
    @Transactional
    public CommonRes updatePostComment(Long postId, Long commentId, PostCommentReq postCommentReq) {
        CommonRes result = new CommonRes();
        try {
            isPostExistById(postId);
            Member loginMember = getLoginMember();
            PostComment postComment = getCommentByPostIdAndCommentId(postId, commentId);

            if (!postComment.getMember().equals(loginMember)) {
                throw new UnauthorizedAccessException(
                        ErrorCode.UNAUTHORIZED.getMessage("작성하지 않은 댓글은 수정할 수 없습니다."));
            }
            if (postComment.isPostCommentChanged(postCommentReq)) {
                postComment.updatePostComment(postCommentReq);
                postCommentRepository.save(postComment);
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
    public CommonRes deletePostComment(Long postId, Long commentId) {
        try {
            isPostExistById(postId);
            Member loginMember = getLoginMember();
            PostComment postComment = getCommentByPostIdAndCommentId(postId, commentId);

            if (!postComment.getMember().equals(loginMember)) {
                throw new UnauthorizedAccessException(
                        ErrorCode.UNAUTHORIZED.getMessage("작성하지 않은 댓글(답글)은 삭제할 수 없습니다."));
            }

            postCommentRepository.delete(postComment);

            return new CommonRes(null, ErrorCode.DELETED.getCode(),
                    ErrorCode.DELETED.getMessage("게시판 댓글(답글)"));

        } catch (NotFoundException | UnauthorizedAccessException e) {
            return ExceptionUtil.handleException(e);
        }

    }

    private PostComment getCommentByPostIdAndCommentId(Long postId, Long commentId) {
        PostComment postComments = postCommentRepository.findByPostPostIdAndCommentId(postId, commentId);

        if (postComments == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시판 댓글"));
        }
        return postComments;
    }
}

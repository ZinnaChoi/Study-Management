package mogakco.StudyManagement.service.post.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.domain.PostComment;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.PostCommentReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.PostCommentRepository;
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

}

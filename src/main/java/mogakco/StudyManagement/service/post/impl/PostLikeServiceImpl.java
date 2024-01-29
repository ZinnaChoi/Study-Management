package mogakco.StudyManagement.service.post.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.domain.PostLike;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.ConflictException;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.PostLikeRepository;
import mogakco.StudyManagement.repository.PostRepository;
import mogakco.StudyManagement.repository.spec.PostLikeSpecification;
import mogakco.StudyManagement.repository.spec.PostSpecification;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostLikeService;
import mogakco.StudyManagement.util.ExceptionUtil;
import mogakco.StudyManagement.util.SecurityUtil;

@Service
public class PostLikeServiceImpl implements PostLikeService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public PostLikeServiceImpl(MemberRepository memberRepository, PostRepository postRepository,
            PostLikeRepository postLikeRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
    }

    @Override
    @Transactional
    public DTOResCommon createPostLike(Long postId, LoggingService lo) {

        try {
            lo.setDBStart();
            Member member = getLoginMember();
            Post post = getPostById(postId);
            lo.setDBEnd();

            Specification<PostLike> postLikeSpec = PostLikeSpecification.withMemberId(member);
            lo.setDBStart();
            long count = postLikeRepository.count(postLikeSpec);
            lo.setDBEnd();

            if (count > 0) {
                throw new ConflictException(ErrorCode.CONFLICT.getMessage(member.getName() + "의 좋아요"));
            }

            PostLike newPostLike = PostLike.builder().member(member).post(post).build();

            lo.setDBStart();
            postLikeRepository.save(newPostLike);
            lo.setDBEnd();

            return new DTOResCommon(null, ErrorCode.CREATED.getCode(), ErrorCode.CREATED.getMessage("게시글 좋아요"));

        } catch (NotFoundException | ConflictException e) {
            return ExceptionUtil.handleException(e);
        }

    }

    @Override
    @Transactional
    public DTOResCommon deletePostLike(Long postId, LoggingService lo) {

        try {
            lo.setDBStart();
            Member member = getLoginMember();
            Post post = getPostById(postId);
            lo.setDBEnd();

            Specification<PostLike> postLikeSpec = PostLikeSpecification.withPostAndMember(post, member);
            lo.setDBStart();
            PostLike postLike = postLikeRepository.findOne(postLikeSpec)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글 좋아요")));
            postLikeRepository.delete(postLike);
            lo.setDBEnd();

            return new DTOResCommon(null, ErrorCode.DELETED.getCode(), ErrorCode.DELETED.getMessage("게시글 좋아요"));

        } catch (NotFoundException e) {
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

}

package mogakco.StudyManagement.service.post.impl;

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
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostCommonService;
import mogakco.StudyManagement.service.post.PostLikeService;
import mogakco.StudyManagement.util.ExceptionUtil;

@Service
public class PostLikeServiceImpl extends PostCommonService implements PostLikeService {

    private final PostLikeRepository postLikeRepository;

    public PostLikeServiceImpl(MemberRepository memberRepository, PostRepository postRepository,
            PostLikeRepository postLikeRepository) {
        super(memberRepository, postRepository);
        this.postLikeRepository = postLikeRepository;
    }

    @Override
    @Transactional
    public DTOResCommon createPostLike(Long postId, LoggingService lo) {

        try {
            lo.setDBStart();
            Member member = getLoginMember();
            Post post = getPostById(postId);
            long count = postLikeRepository.countByMember(member);
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
            PostLike postLike = postLikeRepository.findByPostAndMember(post, member);
            lo.setDBEnd();

            if (postLike == null) {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글 좋아요"));
            }

            lo.setDBStart();
            postLikeRepository.delete(postLike);
            lo.setDBEnd();

            return new DTOResCommon(null, ErrorCode.DELETED.getCode(), ErrorCode.DELETED.getMessage("게시글 좋아요"));

        } catch (NotFoundException e) {
            return ExceptionUtil.handleException(e);
        }
    }
}

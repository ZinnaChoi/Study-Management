package mogakco.StudyManagement.service.post.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.domain.PostLike;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.ConflictException;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.PostLikeRepository;
import mogakco.StudyManagement.repository.PostRepository;
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
    public CommonRes createPostLike(Long postId) {

        try {

            Member member = getLoginMember();
            Post post = getPostById(postId);
            long count = postLikeRepository.countByMemberAndPost(member, post);

            if (count > 0) {
                throw new ConflictException(ErrorCode.CONFLICT.getMessage(member.getName() + "의 좋아요"));
            }

            PostLike newPostLike = PostLike.builder().member(member).post(post).build();

            postLikeRepository.save(newPostLike);

            return new CommonRes(null, ErrorCode.CREATED.getCode(), ErrorCode.CREATED.getMessage("게시글 좋아요"));

        } catch (NotFoundException | ConflictException e) {
            return ExceptionUtil.handleException(e);
        }

    }

    @Override
    @Transactional
    public CommonRes deletePostLike(Long postId) {
        try {

            Member member = getLoginMember();
            Post post = getPostById(postId);
            PostLike postLike = postLikeRepository.findByPostAndMember(post, member);

            if (postLike == null) {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글 좋아요"));
            }

            postLikeRepository.delete(postLike);

            return new CommonRes(null, ErrorCode.DELETED.getCode(), ErrorCode.DELETED.getMessage("게시글 좋아요"));

        } catch (NotFoundException e) {
            return ExceptionUtil.handleException(e);
        }
    }
}

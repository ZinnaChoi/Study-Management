package mogakco.StudyManagement.service.post;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.PostRepository;
import mogakco.StudyManagement.util.SecurityUtil;

public abstract class PostCommonService {

    protected final MemberRepository memberRepository;
    protected final PostRepository postRepository;

    public PostCommonService(MemberRepository memberRepository, PostRepository postRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    protected Member getLoginMember() {
        return memberRepository.findById(SecurityUtil.getLoginUserId());
    }

    protected Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글")));
    }

    protected void isPostExistById(Long postId) {
        if (!postRepository.existsById(postId))
            throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("게시글"));
    }

}
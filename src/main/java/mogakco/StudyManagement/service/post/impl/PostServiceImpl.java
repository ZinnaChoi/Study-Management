package mogakco.StudyManagement.service.post.impl;

import org.springframework.stereotype.Service;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.dto.PostCreateReq;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.PostRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostService;
import mogakco.StudyManagement.util.DateUtil;

@Service
public class PostServiceImpl implements PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public PostServiceImpl(MemberRepository memberRepository, PostRepository postRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void createPost(PostCreateReq postCreateReq, LoggingService lo) {

        // TODO: 로그인한 스터디원 정보 가져오는 기능 구현 필요
        Member member = memberRepository.findById("admin");

        // TODO: 조회수 기능 구현 필요
        Post post = Post.builder().member(member).title(postCreateReq.getTitle()).content(postCreateReq.getContent())
                .viewCnt(0).createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime())
                .build();

        lo.setDBStart();
        postRepository.save(post);
        lo.setDBEnd();
    }

}

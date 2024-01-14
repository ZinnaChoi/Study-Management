package mogakco.StudyManagement.service.post.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.dto.PostCreateReq;
import mogakco.StudyManagement.dto.PostList;
import mogakco.StudyManagement.dto.PostListReq;
import mogakco.StudyManagement.dto.PostListRes;
import mogakco.StudyManagement.dto.SimplePageable;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.enums.PostSearchType;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.PostRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.PageUtil;
import mogakco.StudyManagement.util.SecurityUtil;

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

        Member member = memberRepository.findById(SecurityUtil.getLoginUserId());

        Post post = Post.builder().member(member).title(postCreateReq.getTitle()).content(postCreateReq.getContent())
                .viewCnt(0).createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime())
                .build();

        lo.setDBStart();
        postRepository.save(post);
        lo.setDBEnd();
    }

    @Override
    public PostListRes getPostList(PostListReq postListReq, LoggingService lo, Pageable pageable) {

        Member member = memberRepository.findByName(postListReq.getSearchKeyWord());

        Page<Post> posts = (postListReq.getSerachType() == PostSearchType.TITLE)
                ? postRepository.findByTitle(postListReq.getSearchKeyWord(), pageable)
                : postRepository.findByMember(member, pageable);

        List<PostList> postLists = posts.getContent().stream()
                .map(PostList::new)
                .collect(Collectors.toList());

        SimplePageable simplePageable = PageUtil.createSimplePageable(posts);

        PostListRes result = new PostListRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), postLists,
                simplePageable);

        return result;
    }

}

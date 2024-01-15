package mogakco.StudyManagement.service.post.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.dto.PostReq;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.PostList;
import mogakco.StudyManagement.dto.PostListReq;
import mogakco.StudyManagement.dto.PostListRes;
import mogakco.StudyManagement.dto.SimplePageable;
import mogakco.StudyManagement.enums.ErrorCode;
import java.util.Optional;
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
    public void createPost(PostReq postCreateReq, LoggingService lo) {
        lo.setDBStart();
        Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
        lo.setDBEnd();

        Post post = Post.builder().member(member).title(postCreateReq.getTitle()).content(postCreateReq.getContent())
                .viewCnt(0).createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime())
                .build();

        lo.setDBStart();
        postRepository.save(post);
        lo.setDBEnd();
    }

    @Override
    public PostListRes getPostList(PostListReq postListReq, LoggingService lo, Pageable pageable) {

        Page<Post> posts;
        String searchKeyWord = postListReq.getSearchKeyWord().trim();

        lo.setDBStart();
        if (postListReq.getSearchType() == PostSearchType.TITLE) {
            posts = postRepository.findByTitleContaining(searchKeyWord, pageable);
        } else {
            List<Member> members = memberRepository.findByNameContaining(searchKeyWord);
            posts = postRepository.findByMemberIn(members, pageable);
        }
        lo.setDBEnd();

        List<PostList> postLists = posts.getContent().stream()
                .map(PostList::new)
                .collect(Collectors.toList());

        SimplePageable simplePageable = PageUtil.createSimplePageable(posts);

        PostListRes result = new PostListRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), postLists,
                simplePageable);

        return result;
    }

    @Override
    public DTOResCommon updatePost(Long postId, PostReq postUpdateReq, LoggingService lo) {

        lo.setDBStart();
        Optional<Post> post = postRepository.findByPostId(postId);
        lo.setDBEnd();
        if (!post.isPresent()) {
            return new DTOResCommon(null, ErrorCode.NOT_FOUND.getCode(),
                    ErrorCode.NOT_FOUND.getMessage("게시글"));
        }

        lo.setDBStart();
        Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
        Post postByLoginMember = postRepository.findByPostIdAndMember(postId, member);
        lo.setDBEnd();
        if (postByLoginMember == null) {
            return new DTOResCommon(null, ErrorCode.BAD_REQUEST.getCode(),
                    ErrorCode.BAD_REQUEST.getMessage("작성하지 않은 게시글은 수정할 수 없습니다"));
        }

        if (postByLoginMember.isPostChanged(postUpdateReq)) {
            postByLoginMember.updateTitle(postUpdateReq.getTitle());
            postByLoginMember.updateContent(postUpdateReq.getContent());
            lo.setDBStart();
            postRepository.save(postByLoginMember);
            lo.setDBEnd();
        } else {
            return new DTOResCommon(null, ErrorCode.UNCHANGED.getCode(), ErrorCode.UNCHANGED.getMessage("게시글"));
        }

        return new DTOResCommon(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
    }

}

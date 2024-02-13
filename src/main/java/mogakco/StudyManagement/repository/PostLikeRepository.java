package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.domain.PostLike;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    PostLike findByPostAndMember(Post post, Member member);

    Integer countByMember(Member member);

    Integer countByPostPostId(Long postId);

    Integer countByMemberAndPost(Member member, Post post);
}

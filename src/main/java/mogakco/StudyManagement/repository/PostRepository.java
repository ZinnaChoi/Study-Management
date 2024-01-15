package mogakco.StudyManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByPostId(Long postId);

    Post findByPostIdAndMember(Long postId, Member member);

    Page<Post> findByTitleContaining(String title, Pageable pageable);

    Page<Post> findByMemberIn(List<Member> members, Pageable pageable);

}

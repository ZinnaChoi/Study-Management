package mogakco.StudyManagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTitle(String title, Pageable pageable);

    Page<Post> findByMember(Member member, Pageable pageable);

}

package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

}

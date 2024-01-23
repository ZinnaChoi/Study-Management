package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.PostLike;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long>, JpaSpecificationExecutor<PostLike> {

}

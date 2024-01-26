package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.PostComment;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long>, JpaSpecificationExecutor<PostComment> {

    Integer countByPostPostId(Long postId);

    Integer countByParentCommentCommentId(Long parentCommentId);

}

package mogakco.StudyManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.PostComment;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long>, JpaSpecificationExecutor<PostComment> {

    @Query("SELECT pc FROM PostComment pc WHERE pc.post.postId = :postId AND pc.parentComment IS NULL")
    List<PostComment> findTopLevelCommentsByPostId(@Param("postId") Long postId);

    Integer countByPostPostId(Long postId);

    Integer countByParentCommentCommentId(Long parentCommentId);

    @Query("SELECT pc.parentComment.commentId, COUNT(pc) " +
            "FROM PostComment pc " +
            "WHERE pc.post.postId = :postId AND pc.parentComment IS NOT NULL " +
            "GROUP BY pc.commentId")
    List<Object[]> countRepliesByPostId(@Param("postId") Long postId);

}

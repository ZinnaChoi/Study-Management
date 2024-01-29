package mogakco.StudyManagement.repository.spec;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import mogakco.StudyManagement.domain.PostComment;

public class PostCommentSpecification {

    public static Specification<PostComment> withPostIdAndCommentId(Long postId, Long commentId) {
        return (root, query, criteriaBuilder) -> {
            Predicate postIdPredicate = criteriaBuilder.equal(root.get("post").get("id"), postId);
            Predicate commentIdPredicate = criteriaBuilder.equal(root.get("commentId"), commentId);
            return criteriaBuilder.and(postIdPredicate, commentIdPredicate);
        };
    }

    public static Specification<PostComment> withParentCommentId(Long parentCommentId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.join("parentComment").get("commentId"), parentCommentId);
        };
    }
}

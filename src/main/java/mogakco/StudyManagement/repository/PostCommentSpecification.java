package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import mogakco.StudyManagement.domain.PostComment;

public class PostCommentSpecification {

    public static Specification<PostComment> withCommentId(Long commentId) {
        return (root, query, criteriaBuiler) -> criteriaBuiler.equal(root.get("commentId"), commentId);
    }

    public static Specification<PostComment> withPostIdAndParentCommentIdIsNull(Long postId) {
        return (root, query, criteriaBuilder) -> {
            Predicate postPredicate = criteriaBuilder.equal(root.get("post").get("id"), postId);
            Predicate parentCommentPredicate = criteriaBuilder.isNull(root.get("parentComment"));
            return criteriaBuilder.and(postPredicate, parentCommentPredicate);
        };
    }

    public static Specification<PostComment> withParentCommentId(Long parentCommentId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.join("parentComment").get("commentId"), parentCommentId);
        };
    }
}

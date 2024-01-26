package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.domain.Specification;

import mogakco.StudyManagement.domain.PostComment;

public class PostCommentSpecification {

    public static Specification<PostComment> withCommentId(Long commentId) {
        return (root, query, criteriaBuiler) -> criteriaBuiler.equal(root.get("commentId"), commentId);
    }

    public static Specification<PostComment> withParentCommentId(Long parentCommentId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.join("parentComment").get("commentId"), parentCommentId);
        };
    }
}

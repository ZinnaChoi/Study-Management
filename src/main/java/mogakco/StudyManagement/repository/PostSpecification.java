package mogakco.StudyManagement.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.domain.Member;

public class PostSpecification {

    public static Specification<Post> withPostId(Long postId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("postId"), postId);
    }

    public static Specification<Post> withTitleContaining(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isEmpty()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.like(root.get("title"), "%" + title + "%");
        };
    }

    public static Specification<Post> withMemberIn(List<Member> members) {
        return (root, query, criteriaBuilder) -> root.get("member").in(members);
    }
}

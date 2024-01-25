package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.domain.Specification;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.domain.PostLike;

public class PostLikeSpecification {

    public static Specification<PostLike> withMemberId(Member member) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("member"), member);
    }

    public static Specification<PostLike> withPostAndMember(Post post, Member member) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("post"), post),
                criteriaBuilder.equal(root.get("member"), member));
    }

}

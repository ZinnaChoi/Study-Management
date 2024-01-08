package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mogakco.StudyManagement.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findById(String id);

    Boolean existsById(String id);
}

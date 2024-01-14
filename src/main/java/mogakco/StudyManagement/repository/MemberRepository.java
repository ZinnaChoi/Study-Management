package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findById(String id);

    Member findByName(String name);

    Boolean existsById(String id);
}

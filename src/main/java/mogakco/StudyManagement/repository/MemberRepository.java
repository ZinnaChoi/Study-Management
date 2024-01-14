package mogakco.StudyManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findById(String id);

    List<Member> findByNameContaining(String name);

    Boolean existsById(String id);
}

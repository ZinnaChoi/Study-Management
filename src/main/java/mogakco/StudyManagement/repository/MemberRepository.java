package mogakco.StudyManagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.enums.MemberRole;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findById(String id);

    List<Member> findByNameContaining(String name);

    Member findByName(String name);

    Page<Member> findAllByRoleNot(MemberRole role, Pageable pageable);

    Boolean existsById(String id);
}

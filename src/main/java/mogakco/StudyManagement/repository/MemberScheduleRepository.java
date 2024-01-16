package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.MemberSchedule;

@Repository
public interface MemberScheduleRepository extends JpaRepository<MemberSchedule, Long> {

    List<MemberSchedule> findAllByMember(Member member);
}

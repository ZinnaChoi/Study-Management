package mogakco.StudyManagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.MemberSchedule;
import mogakco.StudyManagement.domain.Schedule;

@Repository
public interface MemberScheduleRepository extends JpaRepository<MemberSchedule, Long> {

    List<MemberSchedule> findAllByMember(Member member);

    Page<MemberSchedule> findAllBySchedule(Schedule schedule, Pageable pageable);

    long countByMember(Member member);
}

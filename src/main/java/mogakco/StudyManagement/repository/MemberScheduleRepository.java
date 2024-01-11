package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.MemberSchedule;

@Repository
public interface MemberScheduleRepository extends JpaRepository<MemberSchedule, Long> {

}

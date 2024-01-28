package mogakco.StudyManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.AbsentSchedule;

@Repository
public interface AbsentScheduleRepository
                extends JpaRepository<AbsentSchedule, Long>, JpaSpecificationExecutor<AbsentSchedule> {
        List<AbsentSchedule> findAbsentSchedulesByMember_MemberIdAndAbsentDate(Long memberId, String absentDate);
}

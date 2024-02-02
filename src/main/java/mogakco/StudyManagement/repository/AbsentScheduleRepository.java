package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import mogakco.StudyManagement.domain.AbsentSchedule;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Schedule;

@Repository
public interface AbsentScheduleRepository
        extends JpaRepository<AbsentSchedule, Long>, JpaSpecificationExecutor<AbsentSchedule> {

    List<AbsentSchedule> findByAbsentDate(String absentDate);

    List<AbsentSchedule> findByAbsentDateAndMember(String absentDate, Member member);

    Integer countByAbsentDateAndScheduleAndMember(String absentDate, Schedule schedule, Member member);

}
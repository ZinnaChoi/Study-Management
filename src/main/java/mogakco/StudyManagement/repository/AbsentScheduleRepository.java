package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.AbsentSchedule;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Schedule;

@Repository
public interface AbsentScheduleRepository extends JpaRepository<AbsentSchedule, Long> {

    int countByAbsentDateAndEventNameAndMember(String absentDate, Schedule schedule, Member memeber);

}

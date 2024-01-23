package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mogakco.StudyManagement.domain.Schedule;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByScheduleNameIn(Iterable<String> scheduleNames);

    Schedule findByScheduleName(String scheduleName);

}

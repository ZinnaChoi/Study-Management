package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import mogakco.StudyManagement.domain.Schedule;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByScheduleNameIn(Iterable<String> scheduleNames);

    Schedule findByScheduleName(String scheduleName);

    @Query("SELECT s FROM Schedule s WHERE s.startTime = :startTime ORDER BY s.startTime ASC LIMIT 1")
    Schedule findScheduleIdMatchingStartTime(@Param("startTime") String startTime);
}

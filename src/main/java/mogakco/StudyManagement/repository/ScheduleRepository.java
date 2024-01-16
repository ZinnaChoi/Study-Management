package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mogakco.StudyManagement.domain.Schedule;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    Boolean existsByEventName(String evnetName);

    List<Schedule> findByEventNameIn(List<String> eventNames);

}

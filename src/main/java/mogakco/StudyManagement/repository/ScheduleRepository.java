package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mogakco.StudyManagement.domain.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {

}

package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.DailyLog;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

}

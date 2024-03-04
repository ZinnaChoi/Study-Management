package mogakco.StudyManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.DailyLog;
import mogakco.StudyManagement.enums.LogType;

@Repository
public interface StatRepository extends JpaRepository<DailyLog, Long> {

    List<DailyLog> findByTypeAndDateBetween(LogType type, String startDate, String endDate);
}

package mogakco.StudyManagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.DailyLog;
import mogakco.StudyManagement.enums.LogType;

@Repository
public interface StatRepository extends JpaRepository<DailyLog, Long> {

    Page<DailyLog> findByType(LogType type, Pageable pageable);
}

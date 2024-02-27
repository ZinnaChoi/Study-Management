package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.DailyLog;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.enums.LogType;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    boolean existsByTypeAndDateAndMember(LogType type, String date, Member member);

}

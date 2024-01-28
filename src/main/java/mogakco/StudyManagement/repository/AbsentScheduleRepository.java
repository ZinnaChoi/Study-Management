package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.AbsentSchedule;

@Repository
public interface AbsentScheduleRepository
                extends JpaRepository<AbsentSchedule, Long>, JpaSpecificationExecutor<AbsentSchedule> {
}

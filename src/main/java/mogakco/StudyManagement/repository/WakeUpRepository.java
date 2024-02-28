package mogakco.StudyManagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.WakeUp;

@Repository
public interface WakeUpRepository extends JpaRepository<WakeUp, Long> {

    WakeUp findByMember(Member member);

    Page<WakeUp> findAllByWakeupTime(String wakeupTime, Pageable pageable);

    List<WakeUp> findAll();

}

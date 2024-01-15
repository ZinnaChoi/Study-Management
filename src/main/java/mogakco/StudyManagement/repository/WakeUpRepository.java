package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.WakeUp;

@Repository
public interface WakeUpRepository extends JpaRepository<WakeUp, Long> {

    WakeUp findByMember(Member member);

}

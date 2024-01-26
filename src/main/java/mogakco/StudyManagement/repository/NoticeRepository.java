package mogakco.StudyManagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findByMember_MemberId(@Param("memberId") Long memberId);

}

package mogakco.StudyManagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.MemberSchedule;
import mogakco.StudyManagement.domain.Schedule;

@Repository
public interface MemberScheduleRepository extends JpaRepository<MemberSchedule, Long> {

    List<MemberSchedule> findAllByMember(Member member);

    Page<MemberSchedule> findAllBySchedule(Schedule schedule, Pageable pageable);

    Integer countByMember(Member member);

    @Query("SELECT ms.member FROM MemberSchedule ms WHERE ms.schedule.scheduleId = :scheduleId")
    List<Member> findMembersByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT NEW map(ms.member.id AS memberId, ms.member.name AS memberName, COUNT(ms) AS maxSchedule) " +
            "FROM MemberSchedule ms " +
            "GROUP BY ms.member.id, ms.member.name")
    List<Map<String, Object>> findMaxSchedulePerMember();
}

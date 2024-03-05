package mogakco.StudyManagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.enums.MemberRole;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findById(String id);

    List<Member> findByNameContaining(String name);

    Member findByName(String name);

    Page<Member> findAllByRoleNot(MemberRole role, Pageable pageable);

    Boolean existsById(String id);

    @Query(value = "SELECT m.id, m.name, " +
            "(SELECT GROUP_CONCAT(DISTINCT s.schedule_name ORDER BY s.schedule_name ASC) FROM member_schedule ms " +
            "INNER JOIN schedule s ON ms.schedule_id = s.schedule_id WHERE ms.member_id = m.member_id) AS schedules, " +
            "w.wakeup_time " +
            "FROM member m " +
            "LEFT JOIN wakeup w ON m.member_id = w.member_id " +
            "WHERE m.id != 'admin' " +
            "GROUP BY m.id, m.name, m.email, w.wakeup_time", nativeQuery = true)
    Page<Object[]> findMembersWithSchedulesAndWakeupTime(Pageable pageable);

    @Query(value = "SELECT m.id, m.name, " +
            "(SELECT GROUP_CONCAT(DISTINCT s.schedule_name ORDER BY s.schedule_name ASC) FROM member_schedule ms " +
            "INNER JOIN schedule s ON ms.schedule_id = s.schedule_id WHERE ms.member_id = m.member_id) AS schedule_name, "
            +
            "w.wakeup_time " +
            "FROM member m " +
            "LEFT JOIN wakeup w ON m.member_id = w.member_id " +
            "INNER JOIN member_schedule ms ON m.member_id = ms.member_id " +
            "INNER JOIN schedule s ON ms.schedule_id = s.schedule_id " +
            "WHERE m.id != 'admin' AND s.schedule_name LIKE %:searchKeyword% " +
            "GROUP BY m.member_id, m.name, m.email, w.wakeup_time", nativeQuery = true)
    Page<Object[]> findMembersWithSchedulesAndWakeupTimeByScheduleName(@Param("searchKeyword") String searchKeyword,
            Pageable pageable);

    @Query(value = "SELECT m.id, m.name, " +
            "(SELECT GROUP_CONCAT(DISTINCT s.schedule_name ORDER BY s.schedule_name ASC) FROM member_schedule ms " +
            "INNER JOIN schedule s ON ms.schedule_id = s.schedule_id WHERE ms.member_id = m.member_id) AS schedule_name, "
            +
            "w.wakeup_time " +
            "FROM member m " +
            "LEFT JOIN wakeup w ON m.member_id = w.member_id " +
            "INNER JOIN member_schedule ms ON m.member_id = ms.member_id " +
            "INNER JOIN schedule s ON ms.schedule_id = s.schedule_id " +
            "WHERE m.id != 'admin' AND TIME_FORMAT(w.wakeup_time, '%H%i') <= TIME_FORMAT(:searchKeyword, '%H%i') " +
            "GROUP BY m.member_id, m.name, m.email, w.wakeup_time", nativeQuery = true)
    Page<Object[]> findMembersWithSchedulesAndWakeupTimeByWakeupTime(@Param("searchKeyword") String searchKeyword,
            Pageable pageable);

}

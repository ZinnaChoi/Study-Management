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

    @Query(value = MemberQueries.BASE_QUERY + MemberQueries.GROUP_BY)
    Page<Object[]> findMembersWithSchedulesAndWakeupTime(Pageable pageable);

    @Query(value = MemberQueries.BASE_QUERY + " AND s.scheduleName LIKE %:searchKeyword% " + MemberQueries.GROUP_BY)
    Page<Object[]> findMembersWithSchedulesAndWakeupTimeByScheduleName(@Param("searchKeyword") String searchKeyword,
            Pageable pageable);

    @Query(value = MemberQueries.BASE_QUERY
            + " AND CASE WHEN :comparisonOperators = '>=' THEN CONCAT(SUBSTRING(w.wakeupTime, 1, 2), ':', SUBSTRING(w.wakeupTime, 3, 2)) >= :searchKeyword"
            + " WHEN :comparisonOperators = '<=' THEN CONCAT(SUBSTRING(w.wakeupTime, 1, 2), ':', SUBSTRING(w.wakeupTime, 3, 2)) <= :searchKeyword END "
            + MemberQueries.GROUP_BY)
    Page<Object[]> findMembersWithSchedulesAndWakeupTimeByWakeupTime(@Param("searchKeyword") String searchKeyword,
            @Param("comparisonOperators") String comparisonOperators,
            Pageable pageable);

}

class MemberQueries {
    public static final String BASE_QUERY = "SELECT m.id, m.name, " +
            "(SELECT GROUP_CONCAT(DISTINCT s.scheduleName) FROM MemberSchedule ms " +
            "INNER JOIN schedule s ON ms.schedule.scheduleId = s.scheduleId WHERE ms.member.memberId = m.memberId) AS schedules, "
            +
            "w.wakeupTime " +
            "FROM Member m " +
            "LEFT JOIN WakeUp w ON m.memberId = w.member.memberId " +
            "INNER JOIN MemberSchedule ms ON m.memberId = ms.member.memberId " +
            "INNER JOIN Schedule s ON ms.schedule.scheduleId = s.scheduleId " +
            "WHERE m.id != 'admin' ";

    public static final String GROUP_BY = " GROUP BY m.memberId, m.name, w.wakeupTime";
}

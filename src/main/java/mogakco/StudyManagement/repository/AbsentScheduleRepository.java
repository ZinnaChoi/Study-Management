package mogakco.StudyManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import mogakco.StudyManagement.domain.AbsentSchedule;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Schedule;

@Repository
public interface AbsentScheduleRepository
                extends JpaRepository<AbsentSchedule, Long>, JpaSpecificationExecutor<AbsentSchedule> {

        @EntityGraph(attributePaths = { "member", "schedule" }, type = EntityGraphType.FETCH)
        List<AbsentSchedule> findByAbsentDate(String absentDate);

        @Query("SELECT a FROM AbsentSchedule a JOIN FETCH a.member JOIN FETCH a.schedule WHERE a.absentDate = :absentDate AND a.member = :member")
        List<AbsentSchedule> findByAbsentDateAndMember(@Param("absentDate") String absentDate,
                        @Param("member") Member member);

        @Query("SELECT COUNT(a) FROM AbsentSchedule a WHERE a.absentDate = :absentDate AND a.schedule = :schedule AND a.member = :member")
        Integer countByAbsentDateAndScheduleAndMember(@Param("absentDate") String absentDate,
                        @Param("schedule") Schedule schedule, @Param("member") Member member);

        @Query("SELECT as.member FROM AbsentSchedule as WHERE as.member = :member AND as.absentDate = :currentDate AND as.schedule = :schedule")
        List<Member> findAbsentParticipants(@Param("member") Member member, @Param("currentDate") String currentDate,
                        @Param("schedule") Schedule schedule);

}

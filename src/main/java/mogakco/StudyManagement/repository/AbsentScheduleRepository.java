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

        List<AbsentSchedule> findByAbsentDate(String absentDate);

        @EntityGraph(attributePaths = { "member" }, type = EntityGraphType.FETCH)
        // @Query("SELECT a FROM AbsentSchedule a JOIN FETCH a.member WHERE a.absentDate
        // = :absentDate AND a.member = :member")
        List<AbsentSchedule> findByAbsentDateAndMember(@Param("absentDate") String absentDate,
                        @Param("member") Member member);

        Integer countByAbsentDateAndScheduleAndMember(@Param("absentDate") String absentDate,
                        @Param("schedule") Schedule schedule, @Param("member") Member member);

        @Query("SELECT as.member FROM AbsentSchedule as WHERE as.member = :member AND as.absentDate = :currentDate AND as.schedule = :schedule")
        List<Member> findAbsentParticipants(@Param("member") Member member, @Param("currentDate") String currentDate,
                        @Param("schedule") Schedule schedule);

}

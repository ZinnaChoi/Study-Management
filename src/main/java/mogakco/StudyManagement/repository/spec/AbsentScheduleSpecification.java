package mogakco.StudyManagement.repository.spec;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import mogakco.StudyManagement.domain.AbsentSchedule;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Schedule;

public class AbsentScheduleSpecification {

    public static Specification<AbsentSchedule> withYearMonth(String yearMonth) {
        return (root, query, criteriaBuilder) -> {
            Expression<String> yearMonthExpression = criteriaBuilder.substring(root.get("absentDate"), 1, 6);
            return criteriaBuilder.equal(yearMonthExpression, yearMonth);
        };
    }

    public static Specification<AbsentSchedule> withAbsentDate(String absentDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("absentDate"), absentDate);
    }

    public static Specification<AbsentSchedule> withMemberIn(List<Member> members) {
        return (root, query, criteriaBuilder) -> root.get("member").in(members);
    }

    public static Specification<AbsentSchedule> withAbsentDateAndMember(String absentDate, Member member) {
        return (root, query, criteriaBuilder) -> {
            Predicate datePredicate = criteriaBuilder.equal(root.get("absentDate"), absentDate);
            Predicate memberPredicate = criteriaBuilder.equal(root.get("member"), member);
            return criteriaBuilder.and(datePredicate, memberPredicate);
        };
    }

    public static Specification<AbsentSchedule> withAbsentDateAndScheduleAndMember(String absentDate, Schedule schedule,
            Member member) {
        return (root, query, criteriaBuilder) -> {
            Predicate datePredicate = criteriaBuilder.equal(root.get("absentDate"), absentDate);
            Predicate schedulePredicate = criteriaBuilder.equal(root.get("schedule"), schedule);
            Predicate memberPredicate = criteriaBuilder.equal(root.get("member"), member);
            return criteriaBuilder.and(datePredicate, schedulePredicate, memberPredicate);
        };
    }
}
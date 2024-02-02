package mogakco.StudyManagement.repository.spec;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Expression;
import mogakco.StudyManagement.domain.AbsentSchedule;
import mogakco.StudyManagement.domain.Member;

public class AbsentScheduleSpecification {

    public static Specification<AbsentSchedule> withYearMonth(String yearMonth) {
        return (root, query, criteriaBuilder) -> {
            Expression<String> yearMonthExpression = criteriaBuilder.substring(root.get("absentDate"), 1, 6);
            return criteriaBuilder.equal(yearMonthExpression, yearMonth);
        };
    }

    public static Specification<AbsentSchedule> withMemberIn(List<Member> members) {
        return (root, query, criteriaBuilder) -> root.get("member").in(members);
    }

}
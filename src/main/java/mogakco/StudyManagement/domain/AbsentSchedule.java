package mogakco.StudyManagement.domain;

import java.util.Objects;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mogakco.StudyManagement.dto.AbsentReq;
import mogakco.StudyManagement.util.DateUtil;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "absent_schedule")
public class AbsentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long absentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Schedule schedule;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String absentDate;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private String updatedAt;

    public void updateAbsentSchedule(AbsentReq absentReq) {
        this.description = absentReq.getDescription();
        this.updatedAt = DateUtil.getCurrentDateTime();
    }

    public boolean isDescriptionChanged(AbsentReq absentReq) {
        return !Objects.equals(this.description, absentReq.getDescription());
    }
}
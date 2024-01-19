package mogakco.StudyManagement.domain;

import java.util.Objects;

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
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_name", nullable = false)
    private Schedule schedule;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String absentDate;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private String updatedAt;

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDescriptionChanged(AbsentReq absentReq) {
        return !Objects.equals(this.description, absentReq.getDescription());
    }
}
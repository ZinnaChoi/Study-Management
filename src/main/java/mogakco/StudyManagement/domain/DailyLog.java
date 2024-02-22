package mogakco.StudyManagement.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.Setter;
import mogakco.StudyManagement.enums.LogType;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "daily_log")
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(nullable = false)
    private String date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType type;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String createdAt;

    public DailyLog(DailyLog dailyLog) {
        this.member = dailyLog.getMember();
        this.date = dailyLog.getDate();
        this.type = dailyLog.getType();
        this.score = dailyLog.getScore();
        this.createdAt = dailyLog.getCreatedAt();
    }

    public DailyLog(Member member, String date, LogType type, int score, String createdAt) {
        this.member = member;
        this.date = date;
        this.type = type;
        this.score = score;
        this.createdAt = createdAt;
    }

}

package mogakco.StudyManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Table(name = "schedule")
public class Schedule {

    @Id
    @Column(nullable = false)
    private String eventName;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;
}

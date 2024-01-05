package mogakco.StudyManagement.entity;

import org.hibernate.annotations.ColumnDefault;

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

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "wakeup", nullable = false)
    @ColumnDefault("true")
    private Boolean wakeup;

    @Column(name = "absent", nullable = false)
    @ColumnDefault("true")
    private Boolean absent;

    @Column(name = "new_post", nullable = false)
    @ColumnDefault("true")
    private Boolean newPost;

    @Column(name = "link_share", nullable = false)
    @ColumnDefault("true")
    private Boolean linkShare;

    @Column(name = "last_share_date", nullable = false)
    private String lastShareDate;
}
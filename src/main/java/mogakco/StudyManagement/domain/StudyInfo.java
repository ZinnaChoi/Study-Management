package mogakco.StudyManagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "study_info")
public class StudyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyId;

    @Column(nullable = false, unique = true)
    private String studyName;

    @Lob
    @Column(nullable = true, columnDefinition = "LONGBLOB")
    private byte[] studyLogo;

    @Column(nullable = false)
    private String db_url;

    @Column(nullable = false)
    private String db_user;

    @Column(nullable = false)
    private String db_password;

    public void updateStudyName(String studyName) {
        this.studyName = studyName;
    }

    public void updateStudyLogo(byte[] studyLogo) {
        this.studyLogo = studyLogo;
    }

}
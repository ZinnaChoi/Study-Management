package mogakco.StudyManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.StudyInfo;

@Repository
public interface StudyInfoRepository extends JpaRepository<StudyInfo, Long> {

}

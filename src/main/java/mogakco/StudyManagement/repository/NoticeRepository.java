package mogakco.StudyManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Notice;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> {

    Optional<Notice> findByMember_MemberId(@Param("memberId") Long memberId);

    Notice findByMember(Member member);

    @Transactional
    @Modifying
    @Query("UPDATE Notice n SET n.lastShareDate = :lastShareDate WHERE n.member.memberId = :memberId")
    void updateLastShareDateByMemberId(@Param("lastShareDate") String lastShareDate, @Param("memberId") Long memberId);

    @Query("SELECT n.member.memberId FROM Notice n ORDER BY n.lastShareDate ASC, n.member.memberId DESC")
    List<Long> findLastShareDateByMemberId();

    @Modifying
    default void insertNoticeForMember(Member member) {
        save(new Notice(member));
    }
}

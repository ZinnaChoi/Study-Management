package mogakco.StudyManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mogakco.StudyManagement.domain.Notice;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findByMember_MemberId(@Param("memberId") Long memberId);

    @Transactional
    @Modifying
    @Query("UPDATE Notice n SET n.lastShareDate = :lastShareDate WHERE n.member.memberId = :memberId")
    void updateLastShareDateByMemberId(@Param("lastShareDate") String lastShareDate, @Param("memberId") Long memberId);

    @Query("SELECT n.member.memberId FROM Notice n ORDER BY n.lastShareDate ASC, n.member.memberId DESC")
    List<Long> findLastShareDateByMemberId();

    @Query("SELECT n.member.memberId FROM Notice n WHERE n.absent = true")
    List<Long> findMemberIdByAbsentIsTrue();

    @Query("SELECT n.member.memberId FROM Notice n WHERE n.newPost = true")
    List<Long> findByNewPostTrue();

    @Query("SELECT n.member.memberId FROM Notice n WHERE n.wakeup = true")
    List<Long> findByWakeupTrue();

}

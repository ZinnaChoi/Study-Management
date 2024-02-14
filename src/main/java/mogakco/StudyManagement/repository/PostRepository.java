package mogakco.StudyManagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    Page<Post> findAll(Pageable pageable);

    boolean existsById(Long postId);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.viewCnt = p.viewCnt + 1 WHERE p.postId = :postId")
    void incrementViewCount(@Param("postId") Long postId);
}

package az.microservices.azmiuuhakaton.repository;

import az.microservices.azmiuuhakaton.enums.SubmissionStatus;
import az.microservices.azmiuuhakaton.model.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByTaskId(Long taskId);

    Optional<Submission> findByTaskIdAndUserId(Long taskId, Long userId);

    List<Submission> findByUserId(Long userId);

    @Query("SELECT s FROM Submission s WHERE s.user.id = :userId AND s.status = :status")
    List<Submission> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") SubmissionStatus status);

    @Query("SELECT s FROM Submission s WHERE s.status = :status")
    List<Submission> findByStatus(@Param("status") SubmissionStatus status);

    boolean existsByTaskIdAndUserId(Long taskId, Long userId);
}

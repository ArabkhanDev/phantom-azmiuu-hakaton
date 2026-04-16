package az.microservices.azmiuuhakaton.repository;

import az.microservices.azmiuuhakaton.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findBySkillId(Long skillId);
}

package az.microservices.azmiuuhakaton.repository;

import az.microservices.azmiuuhakaton.model.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
}


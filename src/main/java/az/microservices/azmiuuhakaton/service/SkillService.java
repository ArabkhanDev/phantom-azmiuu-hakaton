package az.microservices.azmiuuhakaton.service;

import az.microservices.azmiuuhakaton.model.dto.common.SkillDto;
import az.microservices.azmiuuhakaton.model.dto.response.SkillResponse;
import az.microservices.azmiuuhakaton.model.entity.Skill;
import az.microservices.azmiuuhakaton.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll()
                .stream()
                .map(SkillResponse::fromEntity)
                .toList();
    }

    public SkillResponse getSkillById(Long id) {
        return SkillResponse.fromEntity(findSkillById(id));
    }

    @Transactional
    public SkillResponse createSkill(SkillDto dto) {
        requireNonBlank(dto.getTitle(), "title");
        requireNonBlank(dto.getCategory(), "category");

        Skill skill = Skill.builder()
                .title(dto.getTitle())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .difficulty(dto.getDifficulty())
                .iconUrl(dto.getIconUrl())
                .isActive(dto.getActive() != null ? dto.getActive() : true)
                .build();

        return SkillResponse.fromEntity(skillRepository.save(skill));
    }

    @Transactional
    public SkillResponse updateSkill(Long id, SkillDto dto) {
        requireNonBlank(dto.getTitle(), "title");
        requireNonBlank(dto.getCategory(), "category");

        Skill skill = findSkillById(id);

        skill.setTitle(dto.getTitle());
        skill.setCategory(dto.getCategory());
        skill.setDescription(dto.getDescription());
        skill.setDifficulty(dto.getDifficulty());
        skill.setIconUrl(dto.getIconUrl());
        skill.setIsActive(dto.getActive() != null ? dto.getActive() : skill.getIsActive());

        return SkillResponse.fromEntity(skillRepository.save(skill));
    }

    @Transactional
    public void deleteSkill(Long id) {
        Skill skill = findSkillById(id);
        skillRepository.delete(skill);
    }

    private Skill findSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found with id: " + id));
    }

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " can not be empty");
        }
    }
}


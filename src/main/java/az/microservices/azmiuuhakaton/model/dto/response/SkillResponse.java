package az.microservices.azmiuuhakaton.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import az.microservices.azmiuuhakaton.model.entity.Skill;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private String iconUrl;
    private Boolean active;

    public static SkillResponse fromEntity(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getTitle(),
                skill.getDescription(),
                skill.getCategory(),
                skill.getDifficulty(),
                skill.getIconUrl(),
                skill.getIsActive()
        );
    }
}


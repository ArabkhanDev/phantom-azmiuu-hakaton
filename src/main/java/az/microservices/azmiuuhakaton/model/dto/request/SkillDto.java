package az.microservices.azmiuuhakaton.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {

    @NotBlank(message = "title can not be empty")
    private String title;

    @NotBlank(message = "category can not be empty")
    private String category;

    private String description;

    private String difficulty;

    private String iconUrl;

    private Boolean active;
}


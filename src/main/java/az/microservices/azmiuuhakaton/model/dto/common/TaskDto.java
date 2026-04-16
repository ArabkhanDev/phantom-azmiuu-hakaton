package az.microservices.azmiuuhakaton.model.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotNull(message = "skillId can not be null")
    private Long skillId;

    @NotBlank(message = "title can not be empty")
    private String title;

    @NotBlank(message = "description can not be empty")
    private String description;

    private String expectedOutput;

    private String difficulty;

    private Integer durationMinutes;
}

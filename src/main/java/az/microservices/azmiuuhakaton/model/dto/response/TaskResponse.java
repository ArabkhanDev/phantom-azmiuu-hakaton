package az.microservices.azmiuuhakaton.model.dto.response;

import az.microservices.azmiuuhakaton.model.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private Long skillId;
    private String skillTitle;
    private String title;
    private String description;
    private String expectedOutput;
    private String difficulty;
    private Integer durationMinutes;
    private Boolean aiGenerated;

    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getSkill().getId(),
                task.getSkill().getTitle(),
                task.getTitle(),
                task.getDescription(),
                task.getExpectedOutput(),
                task.getDifficulty(),
                task.getDurationMinutes(),
                task.getIsAiGenerated()
        );
    }
}

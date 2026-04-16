package az.microservices.azmiuuhakaton.model.dto.request;

import lombok.Data;

@Data
public class AiGeneratedTaskDto {
    private String title;
    private String description;
    private String expectedOutput;
    private String difficulty;
}
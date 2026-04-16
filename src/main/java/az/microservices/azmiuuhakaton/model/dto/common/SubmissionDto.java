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
public class SubmissionDto {

    @NotNull(message = "taskId can not be null")
    private Long taskId;

    @NotNull(message = "userId can not be null")
    private Long userId;

    @NotBlank(message = "answer can not be empty")
    private String answer;

    private String feedback;

    private Integer score;

    private String status;
}

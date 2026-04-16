package az.microservices.azmiuuhakaton.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIReviewRequest {

    private Long submissionId;
    private Long taskId;
    private String taskTitle;
    private String taskDescription;
    private String taskExpectedOutput;
    private String taskDifficulty;
    private Integer taskDurationMinutes;
    private String submissionAnswer;
    private LocalDateTime submittedAt;
    private Long userId;
    private String userName;
    private String userEmail;
}

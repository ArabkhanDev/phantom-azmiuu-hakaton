package az.microservices.azmiuuhakaton.model.dto.response;

import az.microservices.azmiuuhakaton.enums.SubmissionStatus;
import az.microservices.azmiuuhakaton.model.entity.Submission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {

    private Long id;
    private Long taskId;
    private String taskTitle;
    private Long userId;
    private String userName;
    private String answer;
    private String feedback;
    private Integer score;
    private SubmissionStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime evaluatedAt;

    public static SubmissionResponse fromEntity(Submission submission) {
        return new SubmissionResponse(
                submission.getId(),
                submission.getTask().getId(),
                submission.getTask().getTitle(),
                submission.getUser().getId(),
                submission.getUser().getFullName(),
                submission.getAnswer(),
                submission.getFeedback(),
                submission.getScore(),
                submission.getStatus(),
                submission.getSubmittedAt(),
                submission.getEvaluatedAt()
        );
    }
}

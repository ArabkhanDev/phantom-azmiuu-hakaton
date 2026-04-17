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
public class UserSkillActivationResponse {

    private Long userId;
    private String userName;
    private String userEmail;
    private Double averageScore;
    private Boolean skillsActivated;
    private String message;
    private LocalDateTime activationTime;
    private Integer totalSubmissions;
    private Integer scoredSubmissions;
}

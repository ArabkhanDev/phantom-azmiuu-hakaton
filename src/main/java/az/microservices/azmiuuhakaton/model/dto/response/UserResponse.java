package az.microservices.azmiuuhakaton.model.dto.response;

import az.microservices.azmiuuhakaton.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import az.microservices.azmiuuhakaton.model.dto.response.SkillResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private boolean active;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<SkillResponse> skills;
}

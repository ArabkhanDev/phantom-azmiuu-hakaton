package az.microservices.azmiuuhakaton.model.dto.common;

import az.microservices.azmiuuhakaton.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String fullName;
    private String email;
    private String password;
    private String phone;
    private UserRole role;
    private Boolean active;
    private Boolean emailVerified;
}


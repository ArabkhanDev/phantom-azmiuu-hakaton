package az.microservices.azmiuuhakaton.controller;

import az.microservices.azmiuuhakaton.enums.UserRole;
import az.microservices.azmiuuhakaton.model.dto.request.UserDto;
import az.microservices.azmiuuhakaton.model.dto.response.UserResponse;
import az.microservices.azmiuuhakaton.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getByUserByUsername(username));
    }


    @GetMapping("by-role/{role}")
    public ResponseEntity<List<UserResponse>> getUserByRole(@PathVariable UserRole role) {
        return ResponseEntity.ok(userService.getUserByRole(role));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserDto request) {
        return ResponseEntity.status(CREATED).body(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @RequestBody UserDto request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable Long id,
                                                  @RequestBody UserDto request) {
        return ResponseEntity.ok(userService.patchUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/skills/{skillId}")
    public ResponseEntity<UserResponse> selectSkill(@PathVariable Long userId, @PathVariable Long skillId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String requesterEmail = authentication.getName();
        boolean requesterIsAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> "ROLE_ADMIN".equals("ROLE_ADMIN"));

        return ResponseEntity.ok(userService.selectSkill(userId, skillId, requesterEmail, requesterIsAdmin));
    }
}

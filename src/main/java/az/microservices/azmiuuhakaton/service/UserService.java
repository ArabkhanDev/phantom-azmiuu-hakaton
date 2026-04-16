package az.microservices.azmiuuhakaton.service;

import az.microservices.azmiuuhakaton.enums.UserRole;
import az.microservices.azmiuuhakaton.model.dto.request.UserDto;
import az.microservices.azmiuuhakaton.model.dto.response.UserResponse;
import az.microservices.azmiuuhakaton.model.entity.User;
import az.microservices.azmiuuhakaton.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return toResponse(findUserById(id));
    }

    @Transactional
    public UserResponse createUser(UserDto request) {
        validateEmailUniqueness(request.getEmail(), null);

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole() != null ? request.getRole() : UserRole.USER)
                .isActive(request.getActive() != null ? request.getActive() : true)
                .isEmailVerified(request.getEmailVerified() != null ? request.getEmailVerified() : false)
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserDto request) {
        requireNonBlank(request.getFullName(), "fullName");
        requireNonBlank(request.getEmail(), "email");
        requireNonBlank(request.getPassword(), "password");

        User user = findUserById(id);
        validateEmailUniqueness(request.getEmail(), id);

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole() != null ? request.getRole() : UserRole.USER);
        user.setIsActive(request.getActive() != null ? request.getActive() : true);
        user.setIsEmailVerified(request.getEmailVerified() != null ? request.getEmailVerified() : false);

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse patchUser(Long id, UserDto request) {
        User user = findUserById(id);

        if (request.getEmail() != null) {
            requireNonBlank(request.getEmail(), "email");
            validateEmailUniqueness(request.getEmail(), id);
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            requireNonBlank(request.getFullName(), "fullName");
            user.setFullName(request.getFullName());
        }
        if (request.getPassword() != null) {
            requireNonBlank(request.getPassword(), "password");
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            user.setIsActive(request.getActive());
        }
        if (request.getEmailVerified() != null) {
            user.setIsEmailVerified(request.getEmailVerified());
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    private void validateEmailUniqueness(String email, Long userIdToExclude) {
        userRepository.findByEmail(email)
                .filter(existingUser -> !existingUser.getId().equals(userIdToExclude))
                .ifPresent(existingUser -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
                });
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.getIsActive())
                .emailVerified(user.getIsEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " can not be empty");
        }
    }

    public List<UserResponse> getUserByRole(UserRole role) {
        return userRepository.findByRole(role).stream().map(this::toResponse).toList();
    }
}

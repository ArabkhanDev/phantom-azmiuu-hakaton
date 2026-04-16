package az.microservices.azmiuuhakaton.controller;

import az.microservices.azmiuuhakaton.model.dto.common.TaskDto;
import az.microservices.azmiuuhakaton.model.dto.response.TaskResponse;
import az.microservices.azmiuuhakaton.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/skill/{skillId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskResponse>> getTasksBySkill(@PathVariable Long skillId) {
        return ResponseEntity.ok(taskService.getTasksBySkill(skillId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping("/generate/{skillId}")
    public ResponseEntity<List<TaskResponse>> generateTask(@PathVariable Long skillId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.generateTasks(skillId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDto dto) {
        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }
}

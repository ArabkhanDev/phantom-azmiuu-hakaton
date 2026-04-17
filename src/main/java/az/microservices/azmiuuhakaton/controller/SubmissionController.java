package az.microservices.azmiuuhakaton.controller;

import az.microservices.azmiuuhakaton.enums.SubmissionStatus;
import az.microservices.azmiuuhakaton.model.dto.common.SubmissionDto;
import az.microservices.azmiuuhakaton.model.dto.response.SubmissionResponse;
import az.microservices.azmiuuhakaton.model.dto.response.UserSkillActivationResponse;
import az.microservices.azmiuuhakaton.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubmissionResponse> getSubmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(submissionService.getSubmissionById(id));
    }

    @GetMapping("/task/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubmissionResponse> getSubmissionByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(submissionService.getSubmissionByTaskId(taskId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByUserId(userId));
    }

    @GetMapping("/user/{userId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByUserIdAndStatus(
            @PathVariable Long userId,
            @RequestParam SubmissionStatus status) {
        return ResponseEntity.ok(submissionService.getSubmissionsByUserIdAndStatus(userId, status));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByStatus(@RequestParam SubmissionStatus status) {
        return ResponseEntity.ok(submissionService.getSubmissionsByStatus(status));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponse> createSubmission(@Valid @RequestBody SubmissionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(submissionService.createSubmission(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubmissionResponse> updateSubmission(
            @PathVariable Long id,
            @Valid @RequestBody SubmissionDto dto) {
        return ResponseEntity.ok(submissionService.updateSubmission(id, dto));
    }

    @PutMapping("/{id}/evaluate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<SubmissionResponse> evaluateSubmission(
            @PathVariable Long id,
            @RequestParam String feedback,
            @RequestParam Integer score) {
        return ResponseEntity.ok(submissionService.evaluateSubmission(id, feedback, score));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SubmissionResponse>> getMySubmissions() {
        return ResponseEntity.ok(submissionService.getMySubmissions());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<List<SubmissionResponse>> getPendingSubmissions() {
        return ResponseEntity.ok(submissionService.getPendingSubmissions());
    }

    @PostMapping("/user/{userId}/ai-review")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<List<SubmissionResponse>> aiReviewUserSubmissions(@PathVariable Long userId) {
        return ResponseEntity.ok(submissionService.aiReviewUserSubmissions(userId));
    }

    @PostMapping("/user/{userId}/activate-skills")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<UserSkillActivationResponse> activateUserSkills(@PathVariable Long userId) {
        return ResponseEntity.ok(submissionService.updateUserSkillStatusBasedOnPerformance(userId));
    }

    @PostMapping("/{id}/mentor-review")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<SubmissionResponse> mentorReview(
            @PathVariable Long id,
            @RequestParam String feedback,
            @RequestParam Integer score) {
        return ResponseEntity.ok(submissionService.mentorReview(id, feedback, score));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }
}

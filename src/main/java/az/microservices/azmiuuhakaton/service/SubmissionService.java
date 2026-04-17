package az.microservices.azmiuuhakaton.service;

import az.microservices.azmiuuhakaton.enums.SubmissionStatus;
import az.microservices.azmiuuhakaton.model.dto.common.AIReviewResultDto;
import az.microservices.azmiuuhakaton.model.dto.common.SubmissionDto;
import az.microservices.azmiuuhakaton.model.dto.response.AIReviewRequest;
import az.microservices.azmiuuhakaton.model.dto.response.SubmissionResponse;
import az.microservices.azmiuuhakaton.model.entity.Submission;
import az.microservices.azmiuuhakaton.model.entity.Task;
import az.microservices.azmiuuhakaton.model.entity.User;
import az.microservices.azmiuuhakaton.repository.SubmissionRepository;
import az.microservices.azmiuuhakaton.repository.TaskRepository;
import az.microservices.azmiuuhakaton.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static az.microservices.azmiuuhakaton.enums.SubmissionStatus.AI_REVIEWED;
import static az.microservices.azmiuuhakaton.service.AiServiceImpl.GROQ_URL;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionById(Long id) {
        return SubmissionResponse.fromEntity(findSubmissionById(id));
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionByTaskId(Long taskId) {
        Submission submission = submissionRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found for task id: " + taskId));
        return SubmissionResponse.fromEntity(submission);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByUserId(Long userId) {
        findUserById(userId);
        return submissionRepository.findByUserId(userId)
                .stream()
                .map(SubmissionResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByUserIdAndStatus(Long userId, SubmissionStatus status) {
        findUserById(userId);
        return submissionRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(SubmissionResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByStatus(SubmissionStatus status) {
        return submissionRepository.findByStatus(status)
                .stream()
                .map(SubmissionResponse::fromEntity)
                .toList();
    }

    @Transactional
    public SubmissionResponse createSubmission(SubmissionDto dto) {
        requireNonBlank(dto.getAnswer(), "answer");

        Task task = findTaskById(dto.getTaskId());
        User user = findUserById(dto.getUserId());

        if (submissionRepository.existsByTaskIdAndUserId(dto.getTaskId(), dto.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Submission already exists for this task and user");
        }

        SubmissionStatus status = dto.getStatus() != null ?
                SubmissionStatus.valueOf(dto.getStatus()) : SubmissionStatus.PENDING;

        Submission submission = Submission.builder()
                .task(task)
                .user(user)
                .answer(dto.getAnswer())
                .feedback(dto.getFeedback())
                .score(dto.getScore())
                .status(status)
                .build();

        return SubmissionResponse.fromEntity(submissionRepository.save(submission));
    }

    @Transactional
    public SubmissionResponse updateSubmission(Long id, SubmissionDto dto) {
        requireNonBlank(dto.getAnswer(), "answer");

        Submission submission = findSubmissionById(id);

        submission.setAnswer(dto.getAnswer());
        submission.setFeedback(dto.getFeedback());
        submission.setScore(dto.getScore());
        if (dto.getStatus() != null) {
            submission.setStatus(SubmissionStatus.valueOf(dto.getStatus()));
        }

        return SubmissionResponse.fromEntity(submissionRepository.save(submission));
    }

    @Transactional
    public SubmissionResponse evaluateSubmission(Long id, String feedback, Integer score) {
        Submission submission = findSubmissionById(id);

        submission.setFeedback(feedback);
        submission.setScore(score);
        submission.setStatus(SubmissionStatus.EVALUATED);

        return SubmissionResponse.fromEntity(submissionRepository.save(submission));
    }

    @Transactional
    public void deleteSubmission(Long id) {
        Submission submission = findSubmissionById(id);
        submissionRepository.delete(submission);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getMySubmissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));

        return submissionRepository.findByUserId(currentUser.getId())
                .stream()
                .map(SubmissionResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getPendingSubmissions() {
        return submissionRepository.findByStatus(SubmissionStatus.PENDING)
                .stream()
                .map(SubmissionResponse::fromEntity)
                .toList();
    }

    @Transactional
    public SubmissionResponse mentorReview(Long id, String feedback, Integer score) {
        Submission submission = findSubmissionById(id);

        submission.setFeedback(feedback);
        submission.setScore(score);
        submission.setStatus(SubmissionStatus.MENTOR_REVIEWED);

        return SubmissionResponse.fromEntity(submissionRepository.save(submission));
    }

    @Transactional(readOnly = true)
    public List<AIReviewRequest> getUserSubmissionsForAIReview(Long userId) {
        User user = findUserById(userId);

        List<Submission> submissions = submissionRepository.findByUserId(userId);

        return submissions.stream()
                .map(submission -> AIReviewRequest.builder()
                        .submissionId(submission.getId())
                        .taskId(submission.getTask().getId())
                        .taskTitle(submission.getTask().getTitle())
                        .taskDescription(submission.getTask().getDescription())
                        .taskExpectedOutput(submission.getTask().getExpectedOutput())
                        .taskDifficulty(submission.getTask().getDifficulty())
                        .taskDurationMinutes(submission.getTask().getDurationMinutes())
                        .submissionAnswer(submission.getAnswer())
                        .submittedAt(submission.getSubmittedAt())
                        .userId(user.getId())
                        .userName(user.getFullName())
                        .userEmail(user.getEmail())
                        .build())
                .toList();
    }

    @Transactional
    public List<SubmissionResponse> aiReviewUserSubmissions(Long userId) {

        List<AIReviewRequest> reviewRequests = getUserSubmissionsForAIReview(userId);
        List<SubmissionResponse> results = new ArrayList<>();

        for (AIReviewRequest request : reviewRequests) {

            try {
                AIReviewResultDto aiResult = generateAIReview(request);

                Submission submission = findSubmissionById(request.getSubmissionId());
                submission.setFeedback(aiResult.getFeedback());
                submission.setScore(aiResult.getScore());
                submission.setStatus(AI_REVIEWED);

                Submission saved = submissionRepository.save(submission);
                results.add(SubmissionResponse.fromEntity(saved));

                Thread.sleep(200);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "AI review process was interrupted"
                );
            }
        }

        return results;
    }

    public AIReviewResultDto generateAIReview(AIReviewRequest request) {

        try {
            Map<String, Object> body = new HashMap<>();

            body.put("model", "llama-3.3-70b-versatile");

            List<Map<String, String>> messages = List.of(
                    Map.of(
                            "role", "system",
                            "content",
                            "You are an expert evaluator. Return ONLY valid JSON: feedback, score (0-100)."
                    ),
                    Map.of(
                            "role", "user",
                            "content",
                            buildEvaluationPrompt(request)
                    )
            );

            body.put("messages", messages);
            body.put("temperature", 0.2);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    GROQ_URL,
                    entity,
                    String.class
            );

            String content = extractContent(response.getBody());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, AIReviewResultDto.class);

        } catch (Exception e) {
            throw new RuntimeException("AI review failed", e);
        }
    }

    private String extractContent(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);
        return root
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
    }

    private String buildEvaluationPrompt(AIReviewRequest request) {

        return """
                You are an expert technical evaluator and skill assessor.
                
                Your job is to evaluate a user's submission for a given task.
                
                ---
                
                ## TASK DESCRIPTION
                %s
                
                ---
                
                ## USER SUBMISSION
                %s
                
                ---
                
                ## EVALUATION INSTRUCTIONS
                
                You must:
                1. Evaluate correctness of the answer
                2. Check completeness and logic
                3. Identify which skills are demonstrated
                4. Detect mistakes or missing parts
                5. Assign a fair score from 0 to 100
                
                ---
                
                ## SCORING RULES
                
                - 90–100: Excellent, fully correct, production-level answer
                - 70–89: Mostly correct with minor issues
                - 50–69: Partially correct, missing important parts
                - 20–49: Weak understanding, major issues
                - 0–19: Incorrect or irrelevant
                
                ---
                
                ## IMPORTANT RULES
                - Do NOT hallucinate facts not present in the answer
                - Be strict but fair
                - If answer is empty or irrelevant → score must be 0
                - Keep feedback short, clear, and technical
                - Focus on skill mastery, not writing style
                
                ---
                
                ## OUTPUT FORMAT (STRICT JSON ONLY)
                
                Return ONLY valid JSON. No explanations, no markdown.
                
                {
                  "score": 0-100,
                  "feedback": "short technical feedback",
                  "skills": [
                    "skill1",
                    "skill2"
                  ],
                  "skillValidation": {
                    "skill1": "validated | partially validated | not validated"
                  },
                  "strengths": [
                    "what user did well"
                  ],
                  "weaknesses": [
                    "what is missing or wrong"
                  ]
                }
                
                ---
                
                ## SKILL EXTRACTION RULE
                Identify skills based on task context and user answer.
                Examples:
                - Java
                - Spring Boot
                - REST API
                - SQL
                - System Design
                - Problem Solving
                - Data Structures
                
                If no skill is demonstrated → return empty array.
                
                ---
                
                Now evaluate the submission carefully.
                """.formatted(
                request.getTaskDescription(),
                request.getSubmissionAnswer()
        );
    }

    private Submission findSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found with id: " + id));
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " can not be empty");
        }
    }
}

package az.microservices.azmiuuhakaton.service;

import az.microservices.azmiuuhakaton.model.dto.request.AiGeneratedTaskDto;
import az.microservices.azmiuuhakaton.model.dto.common.TaskDto;
import az.microservices.azmiuuhakaton.model.dto.response.TaskResponse;
import az.microservices.azmiuuhakaton.model.entity.Skill;
import az.microservices.azmiuuhakaton.model.entity.Task;
import az.microservices.azmiuuhakaton.repository.SkillRepository;
import az.microservices.azmiuuhakaton.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final SkillRepository skillRepository;
    private final AiServiceImpl aiService;

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksBySkill(Long skillId) {
        findSkillById(skillId);
        return taskRepository.findBySkillId(skillId)
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        return TaskResponse.fromEntity(findTaskById(id));
    }

    @Transactional
    public TaskResponse createTask(TaskDto dto) {
        requireNonBlank(dto.getTitle(), "title");
        requireNonBlank(dto.getDescription(), "description");

        Skill skill = findSkillById(dto.getSkillId());
        Task task = Task.builder()
                .skill(skill)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .expectedOutput(dto.getExpectedOutput())
                .difficulty(dto.getDifficulty())
                .durationMinutes(dto.getDurationMinutes())
                .isAiGenerated(false)
                .build();

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskDto dto) {
        requireNonBlank(dto.getTitle(), "title");
        requireNonBlank(dto.getDescription(), "description");

        Task task = findTaskById(id);
        Skill skill = findSkillById(dto.getSkillId());

        task.setSkill(skill);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setExpectedOutput(dto.getExpectedOutput());
        task.setDifficulty(dto.getDifficulty());
        task.setDurationMinutes(dto.getDurationMinutes());

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public List<TaskResponse> generateTasks(Long skillId) {
        Skill skill = findSkillById(skillId);

        String prompt = """
        Create exactly 5 learning tasks for the skill: %s
        
        Requirements:
        1. Generate exactly 5 tasks
        2. Include:
           - 1 practical task/question
           - 4 open-ended questions
        3. Tasks should be suitable for an intermediate learner
        4. Return ONLY in this JSON format:
        
        [
          {
            "title": "Task title",
            "description": "Task description",
            "expectedOutput": "Expected result",
            "difficulty": "EASY"
          }
        ]
        """.formatted(skill.getTitle());

        List<AiGeneratedTaskDto> aiTasks = aiService.generateTasks(prompt);

        List<Task> tasks = aiTasks.stream()
                .map(aiTask -> Task.builder()
                        .skill(skill)
                        .title(aiTask.getTitle())
                        .description(aiTask.getDescription())
                        .expectedOutput(aiTask.getExpectedOutput())
                        .difficulty(aiTask.getDifficulty())
                        .durationMinutes(60)
                        .isAiGenerated(true)
                        .build())
                .toList();

        List<Task> savedTasks = taskRepository.saveAll(tasks);

        return savedTasks.stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
    }

    private Skill findSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found with id: " + id));
    }

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " can not be empty");
        }
    }
}

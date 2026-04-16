package az.microservices.azmiuuhakaton.service;
import az.microservices.azmiuuhakaton.model.dto.request.AiGeneratedTaskDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiServiceImpl {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    public List<AiGeneratedTaskDto> generateTasks(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();

            requestBody.put("model", "llama-3.3-70b-versatile");

            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)
            ));

            requestBody.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    GROQ_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) body.get("choices");

            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            String content = (String) message.get("content");

            content = content.replace("```json", "")
                    .replace("```", "")
                    .trim();

            return objectMapper.readValue(
                    content,
                    new TypeReference<List<AiGeneratedTaskDto>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate tasks from AI", e);
        }
    }
}
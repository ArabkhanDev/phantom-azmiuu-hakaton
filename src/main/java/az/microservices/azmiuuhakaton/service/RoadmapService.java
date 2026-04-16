package az.microservices.azmiuuhakaton.service;

import az.microservices.azmiuuhakaton.model.dto.request.GroqRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class RoadmapService {

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateRoadmap(String skill) {

        String prompt = """
                Create a detailed learning roadmap for %s using the exact structure below every time:

                1. Introduction
                - Brief explanation of the skill
                - Why it is useful
                - Career opportunities related to it

                2. Beginner Level
                - Core fundamentals to learn first
                - Recommended beginner topics in logical order
                - Small practical projects for beginners

                3. Intermediate Level
                - More advanced concepts after fundamentals
                - Tools, frameworks, or libraries related to the skill
                - Practical intermediate projects

                4. Advanced Level
                - Expert-level topics
                - Best practices and design principles
                - Real-world advanced projects

                5. Tools & Resources
                - Recommended IDEs / tools
                - Best learning platforms, books, or documentation

                6. Practice Plan
                - Daily or weekly learning schedule
                - Suggested milestones

                7. Final Project Ideas
                - 3 practical final project suggestions for mastering the skill

                Rules:
                - Always follow this exact structure
                - Keep the roadmap practical and step-by-step
                - Organize topics from beginner to advanced
                - Tailor the content specifically for the given skill
                """.formatted(skill);

        GroqRequest requestBody = new GroqRequest(
                "llama-3.3-70b-versatile",
                List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(groqApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GroqRequest> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                groqApiUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return message.get("content").toString();
    }
}
package az.microservices.azmiuuhakaton.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class GroqCvAnalyzerService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String analyzeCv(String cvText) {

        String url = "https://api.groq.com/openai/v1/chat/completions";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");

        List<Map<String, String>> messages = List.of(
                Map.of(
                        "role", "user",
                        "content",
                        """
                        You are an expert academic advisor and CV analyst.
                
                        Analyze the following student's CV and provide:
                
                        1. A professional summary of the student
                        2. Key skills identified from the CV
                        3. Experience level (Beginner / Intermediate / Advanced)
                        4. Strengths
                        5. Weaknesses or missing areas
                        6. Recommended fields of study or university majors that best match the student's profile
                        7. A short explanation for why each field is recommended
                
                        Return the response in this format:
                
                        Professional Summary:
                        ...
                
                        Skills:
                        - ...
                        - ...
                
                        Experience Level:
                        ...
                
                        Strengths:
                        - ...
                        - ...
                
                        Weaknesses:
                        - ...
                        - ...
                
                        Recommended Fields of Study:
                        1. Field Name - explanation
                        2. Field Name - explanation
                        3. Field Name - explanation
                
                        CV:
                        """ + cvText
                )
        );

        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map choice = (Map) ((List) response.getBody().get("choices")).get(0);
        Map message = (Map) choice.get("message");

        return message.get("content").toString();
    }
}
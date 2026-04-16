package az.microservices.azmiuuhakaton.model.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AIReviewResultDto {

    private Integer score;
    private String feedback;

    private List<String> skills;

    private Map<String, String> skillValidation;

    private List<String> strengths;
    private List<String> weaknesses;
}
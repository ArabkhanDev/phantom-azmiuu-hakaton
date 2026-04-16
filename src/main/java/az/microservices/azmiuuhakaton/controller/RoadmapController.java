package az.microservices.azmiuuhakaton.controller;

import az.microservices.azmiuuhakaton.model.dto.request.RoadmapRequest;
import az.microservices.azmiuuhakaton.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @PostMapping
    public String generateRoadmap(@RequestBody RoadmapRequest request) {
        return roadmapService.generateRoadmap(request.getSkill());
    }
}
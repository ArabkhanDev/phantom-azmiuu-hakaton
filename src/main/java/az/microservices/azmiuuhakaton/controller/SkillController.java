package az.microservices.azmiuuhakaton.controller;

import az.microservices.azmiuuhakaton.model.dto.request.SkillDto;
import az.microservices.azmiuuhakaton.model.dto.response.SkillResponse;
import az.microservices.azmiuuhakaton.service.SkillService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAll() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillResponse> create(@Valid @RequestBody SkillDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillResponse> update(@PathVariable Long id, @Valid @RequestBody SkillDto dto) {
        return ResponseEntity.ok(skillService.updateSkill(id, dto));
    }
}


package az.microservices.azmiuuhakaton.controller;

import az.microservices.azmiuuhakaton.service.CvTextExtractorService;
import az.microservices.azmiuuhakaton.service.GroqCvAnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
public class CvAnalysisController {

    private final CvTextExtractorService extractorService;
    private final GroqCvAnalyzerService analyzerService;

    @PostMapping(value = "/analyze", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> analyze(@RequestParam("file") MultipartFile file) throws Exception {

        String cvText = extractorService.extractText(file);
        String result = analyzerService.analyzeCv(cvText);

        return ResponseEntity.ok(result);
    }
}
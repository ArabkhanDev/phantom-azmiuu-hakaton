package az.microservices.azmiuuhakaton.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class CvTextExtractorService {

    public String extractText(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();

        if (fileName.endsWith(".pdf")) {
            return extractPdf(file);
        } else if (fileName.endsWith(".docx")) {
            return extractDocx(file);
        }

        throw new IllegalArgumentException("Unsupported file format");
    }

    private String extractPdf(MultipartFile file) throws Exception {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractDocx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            return extractor.getText();
        }
    }
}
package by.system.gethired.service.doc;

import by.system.gethired.entity.CoverLetterTemplate;
import by.system.gethired.entity.Resume;
import by.system.gethired.entity.User;
import by.system.gethired.repository.CoverLetterTemplateRepository;
import by.system.gethired.repository.ResumeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final ResumeRepository resumeRepository;
    private final CoverLetterTemplateRepository coverLetterRepository;

    @Transactional
    @Override
    public Resume processResumeFile(User user, byte[] content, String originalFilename, String mimeType) {
        String extractedText = extractText(content, originalFilename, mimeType);
        Resume resume = Resume.create(user, extractedText, null);
        resume.setFilePath(originalFilename);
        return resumeRepository.save(resume);
    }

    @Transactional
    @Override
    public Resume saveResumeText(User user, String text) {
        Resume resume = Resume.create(user, text, null);
        return resumeRepository.save(resume);
    }

    @Transactional
    @Override
    public CoverLetterTemplate processCoverLetterFile(User user, byte[] content, String originalFilename, String mimeType) {
        String extractedText = extractText(content, originalFilename, mimeType);
        CoverLetterTemplate template = CoverLetterTemplate.create(user, extractedText);
        return coverLetterRepository.save(template);
    }

    @Transactional
    @Override
    public CoverLetterTemplate saveCoverLetterText(User user, String text) {
        CoverLetterTemplate template = CoverLetterTemplate.create(user, text);
        return coverLetterRepository.save(template);
    }

    @Transactional(readOnly = true)
    @Override
    public Resume getLatestResume(User user) {
        return resumeRepository.findTopByUser_IdOrderByUploadedAtDesc(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No resume found for user " + user.getId()));
    }

    @Transactional(readOnly = true)
    @Override
    public CoverLetterTemplate getLatestCoverLetter(User user) {
        return coverLetterRepository.findTopByUser_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No cover letter template found for user " + user.getId()));
    }

    private String extractText(byte[] content, String originalFilename, String mimeType) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("File content is empty");
        }

        try {
            if (mimeType != null) {
                if (mimeType.contains("pdf")) {
                    return extractTextFromPdf(content);
                } else if (mimeType.contains("wordprocessingml") || mimeType.contains("msword")) {
                    return extractTextFromDocx(content);
                }
            }

            String lowerName = originalFilename.toLowerCase();
            if (lowerName.endsWith(".pdf")) {
                return extractTextFromPdf(content);
            } else if (lowerName.endsWith(".docx")) {
                return extractTextFromDocx(content);
            }

            throw new IllegalArgumentException("Unsupported file type: " + originalFilename);
        } catch (Exception e) {
            log.error("Failed to extract text from file: {}", originalFilename, e);
            throw new RuntimeException("Could not extract text from file: " + e.getMessage(), e);
        }
    }

    private String extractTextFromPdf(byte[] content) throws IOException {
        log.debug("Extracting text from PDF, content length: {}", content.length);
        try (PDDocument document = Loader.loadPDF(content)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.debug("Extracted PDF text length: {}", text != null ? text.length() : 0);
            return text != null ? text.trim() : "";
        }
    }

    private String extractTextFromDocx(byte[] content) throws IOException {
        log.debug("Extracting text from DOCX, content length: {}", content.length);

        try (InputStream inputStream = new ByteArrayInputStream(content);
             XWPFDocument document = new XWPFDocument(inputStream)) {

            StringBuilder sb = new StringBuilder();
            document.getParagraphs().forEach(paragraph -> {
                String text = paragraph.getText();
                if (text != null && !text.isEmpty()) {
                    sb.append(text).append("\n");
                }
            });

            String result = sb.toString().trim();
            log.debug("Extracted DOCX text length: {}", result.length());
            return result;
        }
    }
}
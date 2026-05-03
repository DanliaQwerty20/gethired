package by.system.gethired.service.ai;

import by.system.gethired.entity.CoverLetterTemplate;
import by.system.gethired.entity.Resume;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.service.doc.DocumentService;
import by.system.gethired.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiGenerationServiceImpl implements AiGenerationService {

    private final ChatClient chatClient;
    private final UserService userService;
    private final DocumentService documentService;

    @Override
    public String generateResume(Long chatId, Vacancy vacancy) {
        var user = userService.getByChatId(chatId);
        Resume resume = documentService.getLatestResume(user);

        PromptTemplate template = new PromptTemplate("""
                Ты — профессиональный карьерный консультант.
                На основе исходного резюме и описания вакансии создай адаптированное резюме,
                которое максимально релевантно требованиям работодателя.
                
                Исходное резюме:
                {resume}
                
                Вакансия: {jobTitle}
                Описание вакансии: {jobDescription}
                
                Верни только готовый текст резюме. Не добавляй никаких пояснений.
                """);

        Prompt prompt = template.create(Map.of(
                "resume", resume.getExtractedText(),
                "jobTitle", vacancy.getTitle(),
                "jobDescription", vacancy.getDescription()
        ));

        String generated = chatClient.call(prompt).getResult().getOutput().getContent();
        log.info("Generated resume for user {} and vacancy {}", chatId, vacancy.getExternalId());
        return generated;
    }

    @Override
    public String generateCoverLetter(Long chatId, Vacancy vacancy) {
        var user = userService.getByChatId(chatId);
        CoverLetterTemplate templateEntity = documentService.getLatestCoverLetter(user);

        PromptTemplate template = new PromptTemplate("""
                Ты — профессиональный карьерный консультант.
                На основе примера сопроводительного письма и описания вакансии
                создай персонализированное сопроводительное письмо.
                
                Пример письма:
                {coverLetterExample}
                
                Вакансия: {jobTitle}
                Описание вакансии: {jobDescription}
                
                Верни только готовый текст письма. Не добавляй никаких пояснений.
                """);

        Prompt prompt = template.create(Map.of(
                "coverLetterExample",
                templateEntity != null ? templateEntity.getTemplateText() : "Стандартный шаблон отсутствует.",
                "jobTitle", vacancy.getTitle(),
                "jobDescription", vacancy.getDescription()
        ));

        String generated = chatClient.call(prompt).getResult().getOutput().getContent();
        log.info("Generated cover letter for user {} and vacancy {}", chatId, vacancy.getExternalId());
        return generated;
    }
}
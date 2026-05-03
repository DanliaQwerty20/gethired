package by.system.gethired.handler.command.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.Resume;
import by.system.gethired.entity.User;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.handler.command.CommandHandler;
import by.system.gethired.repository.VacancyRepository;
import by.system.gethired.service.doc.DocumentService;
import by.system.gethired.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendCommandHandler implements CommandHandler {

    private final UserService userService;
    private final DocumentService documentService;
    private final EmbeddingClient embeddingClient;
    private final VacancyRepository vacancyRepository;
    private final TelegramClient telegramClient;

    private static final int CHUNK_SIZE = 500;
    private static final int OVERLAP = 100;

    @Override
    public boolean supports(String command) {
        return "/recommend".equals(command);
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        User user = userService.getByChatId(chatId);

        Resume resume;
        try {
            resume = documentService.getLatestResume(user);
        } catch (EntityNotFoundException e) {
            telegramClient.sendMessage(chatId, "❌ Загрузи резюме через /upload, чтобы получить рекомендации");
            return;
        }

        String resumeText = resume.getExtractedText();
        if (resumeText == null || resumeText.isBlank()) {
            telegramClient.sendMessage(chatId, "❌ Резюме пустое");
            return;
        }

        // Разбиваем на чанки
        List<String> chunks = splitText(resumeText, CHUNK_SIZE, OVERLAP);
        // Получаем эмбеддинги для всех чанков
        List<List<Double>> allEmbeddings = embeddingClient.embed(chunks);
        // Усредняем векторы
        List<Double> averagedVector = averageVectors(allEmbeddings);

        String vectorLiteral = averagedVector.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "[", "]"));

        List<Vacancy> similar = vacancyRepository.findSimilarVacancies(vectorLiteral, 5);

        if (similar.isEmpty()) {
            telegramClient.sendMessage(chatId, "🔍 Похожих вакансий не найдено.");
            return;
        }

        for (Vacancy v : similar) {
            long extId = v.getExternalId();
            String msg = String.format("🎯 **%s**\n📍 %s\n💰 %s\n%s",
                    v.getTitle(), v.getLocation(), v.getSalary(), v.getUrl());
            InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                    .keyboardRow(List.of(
                            InlineKeyboardButton.builder()
                                    .text("📄 Резюме")
                                    .callbackData("gen_resume:" + extId)
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text("✉️ Письмо")
                                    .callbackData("gen_letter:" + extId)
                                    .build()
                    ))
                    .build();
            telegramClient.sendMessageWithInlineKeyboard(chatId, msg, keyboard);
        }
    }

    private List<String> splitText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start += (chunkSize - overlap);
        }
        return chunks;
    }

    private List<Double> averageVectors(List<List<Double>> vectors) {
        if (vectors.isEmpty()) return List.of();
        int dim = vectors.get(0).size();
        List<Double> sum = new ArrayList<>(dim);
        for (int i = 0; i < dim; i++) sum.add(0.0);

        for (List<Double> vec : vectors) {
            for (int i = 0; i < dim; i++) {
                sum.set(i, sum.get(i) + vec.get(i));
            }
        }
        int count = vectors.size();
        for (int i = 0; i < dim; i++) {
            sum.set(i, sum.get(i) / count);
        }
        return sum;
    }
}
package by.system.gethired.service.matching;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.UserFilter;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.repository.VacancyRepository;
import by.system.gethired.service.embedding.VacancyEmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final VacancyRepository vacancyRepository;
    private final TelegramClient telegramClient;
    private final VacancyEmbeddingService embeddingService;

    @Override
    @Transactional
    public void processNewVacancies(UserFilter filter, List<Vacancy> vacancies) {
        if (vacancies == null || vacancies.isEmpty()) return;

        long chatId = filter.getUser().getChatId();
        for (Vacancy v : vacancies) {
            if (!vacancyRepository.existsByExternalId(v.getExternalId())) {
                if (matchesKeywords(filter, v.getDescription()) && matchesSalary(filter, v.getSalary())) {
                    vacancyRepository.save(v);
                    // Сохраняем эмбеддинг описания
                    embeddingService.saveEmbedding(v.getExternalId(), buildText(v));
                    sendVacancyWithButtons(chatId, v);
                }
            }
        }
    }

    private void sendVacancyWithButtons(long chatId, Vacancy v) {
        String message = String.format("""
                🔔 **%s**
                📍 %s
                💰 %s
                📝 %s
                %s
                """, v.getTitle(), v.getLocation(), v.getSalary(), v.getDescription(), v.getUrl());

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(
                        InlineKeyboardButton.builder()
                                .text("📄 Сгенерировать резюме")
                                .callbackData("gen_resume:" + v.getExternalId())
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("✉️ Сопроводительное письмо")
                                .callbackData("gen_letter:" + v.getExternalId())
                                .build()
                ))
                .build();

        telegramClient.sendMessageWithInlineKeyboard(chatId, message, keyboard);
    }

    private boolean matchesKeywords(UserFilter filter, String description) {
        if (description == null) return true;
        String text = description.toLowerCase();

        if (filter.getIncludeKeywords() != null && !filter.getIncludeKeywords().isBlank()) {
            String[] includes = filter.getIncludeKeywords().toLowerCase().split(",\\s*");
            for (String inc : includes) {
                if (!text.contains(inc.trim())) return false;
            }
        }

        if (filter.getExcludeKeywords() != null && !filter.getExcludeKeywords().isBlank()) {
            String[] excludes = filter.getExcludeKeywords().toLowerCase().split(",\\s*");
            for (String exc : excludes) {
                if (text.contains(exc.trim())) return false;
            }
        }
        return true;
    }

    private boolean matchesSalary(UserFilter filter, String salaryText) {
        // Простая проверка (можно улучшить)
        return true;
    }

    private String buildText(Vacancy v) {
        return (v.getTitle() != null ? v.getTitle() : "") + " "
                + (v.getDescription() != null ? v.getDescription() : "");
    }
}
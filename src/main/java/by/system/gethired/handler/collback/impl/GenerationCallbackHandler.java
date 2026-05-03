package by.system.gethired.handler.collback.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.User;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.handler.collback.CallbackHandler;
import by.system.gethired.repository.VacancyRepository;
import by.system.gethired.service.ai.AiGenerationService;
import by.system.gethired.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class GenerationCallbackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final VacancyRepository vacancyRepository;
    private final AiGenerationService aiGenerationService;
    private final UserService userService;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        return data != null && (data.startsWith("gen_resume:") || data.startsWith("gen_letter:"));
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        String[] parts = data.split(":", 2);
        if (parts.length != 2) return;

        String action = parts[0];
        Long vacancyId;
        try {
            vacancyId = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            telegramClient.sendMessage(chatId, "❌ Неверный ID вакансии");
            return;
        }

        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElse(null);
        if (vacancy == null) {
            telegramClient.sendMessage(chatId, "❌ Вакансия не найдена в базе");
            return;
        }

        User user = userService.getByChatId(chatId);

        // Показываем "печатает..."
        telegramClient.sendChatAction(chatId, "typing");

        try {
            String result;
            if ("gen_resume".equals(action)) {
                result = aiGenerationService.generateResume(chatId, vacancy);
            } else {
                result = aiGenerationService.generateCoverLetter(chatId, vacancy);
            }
            if (result.length() > 4096) {
                result = result.substring(0, 4000) + "\n... (обрезано)";
            }
            telegramClient.sendMessage(chatId, "✨ Готово:\n" + result);
        } catch (Exception e) {
            telegramClient.sendMessage(chatId, "❌ Ошибка при генерации: " + e.getMessage());
        }
    }
}
package by.system.gethired.handler.message.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.User;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.enums.UserState;
import by.system.gethired.handler.message.MessageHandler;
import by.system.gethired.repository.VacancyRepository;
import by.system.gethired.service.ai.AiGenerationService;
import by.system.gethired.service.user.UserService;
import by.system.gethired.util.StateComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class VacancyIdHandler implements MessageHandler {

    private final StateComponent stateComponent;
    private final UserService userService;
    private final VacancyRepository vacancyRepository;
    private final AiGenerationService aiGenerationService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(UserState state, Message message) {
        return state == UserState.WAITING_FOR_VACANCY_ID_FOR_GENERATION && message.hasText();
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText().trim();
        User user = userService.getByChatId(chatId);

        try {
            Long vacId = Long.parseLong(text);
            Vacancy vacancy = vacancyRepository.findById(vacId)
                    .orElseThrow(() -> new IllegalArgumentException("Вакансия с таким ID не найдена"));

            telegramClient.sendChatAction(chatId, "typing");
            String generatedResume = aiGenerationService.generateResume(chatId, vacancy);
            telegramClient.sendMessage(chatId, "✨ Адаптированное резюме:\n\n" + generatedResume);
        } catch (NumberFormatException e) {
            telegramClient.sendMessage(chatId, "⚠️ Введи числовой ID вакансии (можно получить через /vacancies)");
        } catch (Exception e) {
            telegramClient.sendMessage(chatId, "❌ Ошибка: " + e.getMessage());
        } finally {
            stateComponent.clear(chatId);
        }
    }
}
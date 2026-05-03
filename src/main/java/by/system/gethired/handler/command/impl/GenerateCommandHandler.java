package by.system.gethired.handler.command.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.User;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.handler.command.CommandHandler;
import by.system.gethired.service.ai.AiGenerationService;
import by.system.gethired.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class GenerateCommandHandler implements CommandHandler {

    private final UserService userService;
    private final AiGenerationService aiGenerationService;
    private final TelegramClient telegramClient;
    // В реальном коде потребуется VacancyRepository для поиска вакансии по ID

    @Override
    public boolean supports(String command) {
        return "/generate".equals(command);
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        User user = userService.getByChatId(chatId);

        // Показываем inline-клавиатуру с выбором вакансии (упрощённо)
        String text = message.getText();
        String[] parts = text.split("\\s+", 2);
        if (parts.length < 2) {
            telegramClient.sendMessage(chatId,
                    "ℹ️ Использование: /generate <id вакансии>\n" +
                            "Чтобы узнать ID вакансии, сначала выполните /vacancies"
            );
            return;
        }

        // Заглушка: в реальном коде берём вакансию из БД
        Vacancy vacancy = new Vacancy();
        vacancy.setExternalId(Long.parseLong(parts[1]));
        vacancy.setTitle("Пример вакансии");
        vacancy.setDescription("Описание вакансии");

        telegramClient.sendChatAction(chatId, "typing");
        String generatedResume = aiGenerationService.generateResume(chatId, vacancy);
        telegramClient.sendMessage(chatId, "✨ Сгенерированное резюме:\n\n" + generatedResume);
    }
}
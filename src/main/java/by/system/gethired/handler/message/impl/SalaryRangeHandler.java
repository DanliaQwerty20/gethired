package by.system.gethired.handler.message.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.User;
import by.system.gethired.enums.UserState;
import by.system.gethired.handler.message.MessageHandler;
import by.system.gethired.service.filter.FilterService;
import by.system.gethired.service.user.UserService;
import by.system.gethired.util.StateComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class SalaryRangeHandler implements MessageHandler {

    private final StateComponent stateComponent;
    private final UserService userService;
    private final FilterService filterService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(UserState state, Message message) {
        return state == UserState.WAITING_FOR_SALARY_RANGE && message.hasText();
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        User user = userService.getByChatId(chatId);

        try {
            if ("0".equals(text.trim())) {
                filterService.updateSalaryFrom(user, null);
                filterService.updateSalaryTo(user, null);
            } else {
                String[] parts = text.split("-");
                if (parts.length == 2) {
                    int from = Integer.parseInt(parts[0].trim());
                    int to = Integer.parseInt(parts[1].trim());
                    filterService.updateSalaryFrom(user, from);
                    filterService.updateSalaryTo(user, to);
                } else {
                    telegramClient.sendMessage(chatId, "Введи в формате \"от-до\", например: 150000-250000");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            telegramClient.sendMessage(chatId, "Неверный формат. Пример: 150000-250000 или 0 для пропуска.");
            return;
        }

        stateComponent.setState(chatId, UserState.WAITING_FOR_INCLUDE_KEYWORDS);
        telegramClient.sendMessage(chatId,
                "✅ Зарплатная вилка сохранена.\n\n" +
                        "Шаг 4/5: Ключевые слова, которые ОБЯЗАТЕЛЬНО должны быть в вакансии (через запятую).\n" +
                        "Например: Spring, Kafka, AWS\n" +
                        "Если не важно — напиши 0:"
        );
    }
}
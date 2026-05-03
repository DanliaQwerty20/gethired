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
public class LocationHandler implements MessageHandler {

    private final StateComponent stateComponent;
    private final UserService userService;
    private final FilterService filterService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(UserState state, Message message) {
        return state == UserState.WAITING_FOR_LOCATION && message.hasText();
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        User user = userService.getByChatId(chatId);
        filterService.updateLocation(user, text);

        stateComponent.setState(chatId, UserState.WAITING_FOR_SALARY_RANGE);
        telegramClient.sendMessage(chatId,
                "✅ Локация сохранена: " + text + "\n\n" +
                        "Шаг 3/5: Введи зарплатную вилку в формате \"от-до\" (например: 150000-250000).\n" +
                        "Если не важно — напиши 0:"
        );
    }
}
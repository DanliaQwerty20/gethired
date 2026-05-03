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
public class IncludeKeywordsHandler implements MessageHandler {

    private final StateComponent stateComponent;
    private final UserService userService;
    private final FilterService filterService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(UserState state, Message message) {
        return state == UserState.WAITING_FOR_INCLUDE_KEYWORDS && message.hasText();
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        User user = userService.getByChatId(chatId);

        if ("0".equals(text.trim())) {
            filterService.updateIncludeKeywords(user, "");
        } else {
            filterService.updateIncludeKeywords(user, text);
        }

        stateComponent.setState(chatId, UserState.WAITING_FOR_EXCLUDE_KEYWORDS);
        telegramClient.sendMessage(chatId,
                "✅ Ключевые слова сохранены.\n\n" +
                        "Шаг 5/5: Слова-исключения (что НЕ должно встречаться в вакансии).\n" +
                        "Например: 1C, PHP, Support\n" +
                        "Если не важно — напиши 0:"
        );
    }
}
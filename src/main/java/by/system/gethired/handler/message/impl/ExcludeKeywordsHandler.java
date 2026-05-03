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
public class ExcludeKeywordsHandler implements MessageHandler {

    private final StateComponent stateComponent;
    private final UserService userService;
    private final FilterService filterService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(UserState state, Message message) {
        return state == UserState.WAITING_FOR_EXCLUDE_KEYWORDS && message.hasText();
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        User user = userService.getByChatId(chatId);

        if ("0".equals(text.trim())) {
            filterService.updateExcludeKeywords(user, "");
        } else {
            filterService.updateExcludeKeywords(user, text);
        }

        stateComponent.clear(chatId);
        telegramClient.sendMessage(chatId,
                "🎉 Настройка завершена!\n\n" +
                        "Теперь ты можешь:\n" +
                        "• /vacancies — найти подходящие вакансии\n" +
                        "• /upload — загрузить резюме\n" +
                        "• /settings — изменить фильтры"
        );
    }
}
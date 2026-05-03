package by.system.gethired.handler.command.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.enums.UserState;
import by.system.gethired.handler.command.CommandHandler;
import by.system.gethired.util.StateComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class SettingsCommandHandler implements CommandHandler {

    private final StateComponent stateComponent;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(String command) {
        return "/settings".equals(command);
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        stateComponent.setState(chatId, UserState.WAITING_FOR_JOB_TITLE);
        telegramClient.sendMessage(chatId,
                "Давай настроим фильтры поиска. Шаг 1/5:\n\n" +
                        "Введи желаемую должность (например: Java Developer, Senior Python Developer):"
        );
    }
}
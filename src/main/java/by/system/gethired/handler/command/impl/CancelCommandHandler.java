package by.system.gethired.handler.command.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.handler.command.CommandHandler;
import by.system.gethired.util.StateComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class CancelCommandHandler implements CommandHandler {

    private final StateComponent stateComponent;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(String command) {
        return "/cancel".equals(command);
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        stateComponent.clear(chatId);
        telegramClient.sendMessage(chatId, "✅ Текущее действие отменено. Ты снова в главном меню.");
    }
}
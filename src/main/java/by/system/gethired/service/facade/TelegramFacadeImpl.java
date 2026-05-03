package by.system.gethired.service.facade;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.controller.dto.TelegramUpdateDto;
import by.system.gethired.handler.UpdateDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramFacadeImpl implements TelegramFacade {
    private final UpdateDispatcher dispatcher;
    private final TelegramClient telegramClient;

    @Async
    @Override
    public void process(TelegramUpdateDto update) {
        if (update.getMessage() != null) {
            Long chatId = update.getMessage().getChatId();
            telegramClient.sendChatAction(chatId, "typing");
            dispatcher.dispatch(update);
        } else if (update.getCallbackQuery() != null) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            telegramClient.sendChatAction(chatId, "typing");
            dispatcher.dispatchCallback(update);
        }
    }
}
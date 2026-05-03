package by.system.gethired.handler;

import by.system.gethired.controller.dto.TelegramUpdateDto;
import by.system.gethired.enums.UserState;
import by.system.gethired.handler.collback.CallbackHandler;
import by.system.gethired.handler.command.CommandHandler;
import by.system.gethired.handler.message.MessageHandler;
import by.system.gethired.util.StateComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateDispatcher {
    private final List<CommandHandler> commandHandlers;
    private final List<MessageHandler> messageHandlers;
    private final List<CallbackHandler> callbackHandlers;
    private final StateComponent stateComponent;
    private final CommandDetector commandDetector;

    public void dispatch(TelegramUpdateDto update) {
        Message message = update.getMessage();
        if (message == null) return;

        if (commandDetector.isCommand(message)) {
            String command = commandDetector.extractCommand(message);
            commandHandlers.stream()
                    .filter(h -> h.supports(command))
                    .findFirst()
                    .ifPresent(h -> h.handle(message));
            return;
        }

        // Обработка
        Long chatId = message.getChatId();
        UserState state = stateComponent.getState(chatId);
        messageHandlers.stream()
                .filter(h -> h.supports(state, message))
                .findFirst()
                .ifPresent(h -> h.handle(message));
    }

    public void dispatchCallback(TelegramUpdateDto update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (callbackQuery == null) return;

        callbackHandlers.stream()
                .filter(h -> h.supports(callbackQuery))
                .findFirst()
                .ifPresent(h -> h.handle(callbackQuery));
    }
}
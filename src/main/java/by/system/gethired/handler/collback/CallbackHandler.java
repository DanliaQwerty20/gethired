package by.system.gethired.handler.collback;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {
    boolean supports(CallbackQuery callbackQuery);
    void handle(CallbackQuery callbackQuery);
}
package by.system.gethired.handler.message;

import by.system.gethired.enums.UserState;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageHandler {
    boolean supports(UserState state, Message message);

    void handle(Message message);
}
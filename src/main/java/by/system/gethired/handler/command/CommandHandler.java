package by.system.gethired.handler.command;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandHandler {
    boolean supports(String command);
    void handle(Message message);
}
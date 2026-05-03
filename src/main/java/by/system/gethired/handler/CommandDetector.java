package by.system.gethired.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CommandDetector {
    public boolean isCommand(Message message) {
        if (message == null || !message.hasText()) return false;
        String text = message.getText();
        return text.startsWith("/");
    }

    public String extractCommand(Message message) {
        if (!message.hasText()) return "";
        String[] parts = message.getText().split("\\s+", 2);
        return parts[0];
    }
}

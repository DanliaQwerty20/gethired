package by.system.gethired.handler.command.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.Resume;
import by.system.gethired.entity.User;
import by.system.gethired.handler.command.CommandHandler;
import by.system.gethired.service.doc.DocumentService;
import by.system.gethired.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class MyResumeCommandHandler implements CommandHandler {
    private final UserService userService;
    private final DocumentService documentService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(String command) {
        return "/myresume".equals(command);
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        User user = userService.getByChatId(chatId);
        try {
            Resume resume = documentService.getLatestResume(user);
            String text = resume.getExtractedText();
            if (text.length() > 4000) {
                text = text.substring(0, 4000) + "\n... (показано частично)";
            }
            telegramClient.sendMessage(chatId, "📄 Твоё последнее резюме:\n" + text);
        } catch (EntityNotFoundException e) {
            telegramClient.sendMessage(chatId, "❌ У тебя ещё нет загруженного резюме. Используй /upload");
        }
    }
}
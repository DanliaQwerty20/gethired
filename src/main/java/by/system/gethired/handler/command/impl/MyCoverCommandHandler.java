package by.system.gethired.handler.command.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.CoverLetterTemplate;
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
public class MyCoverCommandHandler implements CommandHandler {

    private final UserService userService;
    private final DocumentService documentService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(String command) {
        return "/mycover".equals(command);
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        User user = userService.getByChatId(chatId);
        try {
            CoverLetterTemplate template = documentService.getLatestCoverLetter(user);
            String text = template.getTemplateText();
            if (text.length() > 4000) {
                text = text.substring(0, 4000) + "\n... (показано частично)";
            }
            telegramClient.sendMessage(chatId, "✉️ Твой шаблон письма:\n" + text);
        } catch (EntityNotFoundException e) {
            telegramClient.sendMessage(chatId, "❌ У тебя нет загруженного образца письма. Используй /upload");
        }
    }
}
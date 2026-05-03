package by.system.gethired.handler.message.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.User;
import by.system.gethired.enums.UserState;
import by.system.gethired.handler.message.MessageHandler;
import by.system.gethired.service.doc.DocumentService;
import by.system.gethired.service.user.UserService;
import by.system.gethired.util.StateComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class ResumeFileHandler implements MessageHandler {

    private final StateComponent stateComponent;
    private final UserService userService;
    private final DocumentService documentService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(UserState state, Message message) {
        return state == UserState.WAITING_FOR_RESUME_FILE
                && message.hasDocument()
                && isResumeFileName(message.getDocument());
    }

    private boolean isResumeFileName(Document doc) {
        String name = doc.getFileName() != null ? doc.getFileName().toLowerCase() : "";
        // Считаем, что файл — резюме, если в имени есть "resume" или это pdf/docx
        return name.contains("resume") || name.endsWith(".pdf") || name.endsWith(".docx");
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        Document document = message.getDocument();

        User user = userService.getByChatId(chatId);

        try {
            byte[] content = telegramClient.downloadFile(document.getFileId());
            documentService.processResumeFile(user, content, document.getFileName(), document.getMimeType());
            telegramClient.sendMessage(chatId, "✅ Резюме загружено и обработано!");
        } catch (Exception e) {
            telegramClient.sendMessage(chatId, "❌ Ошибка при обработке файла: " + e.getMessage());
        } finally {
            stateComponent.clear(chatId);
        }
    }
}
package by.system.gethired.client;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

@Component
public class TelegramClient {

    @Getter
    private final DefaultAbsSender sender;
    private final String botToken;

    public TelegramClient(DefaultAbsSender sender, @Value("${telegram.bot.token}") String token) {
        this.sender = sender;
        this.botToken = token;
    }

    public void sendMessage(Long chatId, String text) {
        try {
            sender.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public void sendChatAction(Long chatId, String action) {
        try {
            sender.execute(SendChatAction.builder()
                    .chatId(chatId.toString())
                    .action(action)
                    .build());
        } catch (TelegramApiException ignored) {

        }
    }

    public byte[] downloadFile(String fileId) {
        try {
            File tgFile = sender.execute(GetFile.builder().fileId(fileId).build());
            String fileUrl = tgFile.getFileUrl(botToken);
            try (InputStream is = new URL(fileUrl).openStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return baos.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file " + fileId, e);
        }
    }
}
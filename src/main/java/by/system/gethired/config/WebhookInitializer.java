package by.system.gethired.config;

import by.system.gethired.client.TelegramClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookInitializer {
    @Value("${telegram.bot.webhook-path}")
    private String webhookPath;
    @Value("${app.public-url:}")
    private String publicUrl;

    private final TelegramClient client;

    @EventListener(ApplicationReadyEvent.class)
    public void setWebhook() {
        if (publicUrl.isEmpty()) {
            log.warn("No public URL configured, webhook not set");
            return;
        }
        String url = publicUrl + webhookPath;
        try {
            client.getSender().execute(SetWebhook.builder().url(url).build());
            log.info("Webhook set to {}", url);
        } catch (Exception e) {
            log.error("Failed to set webhook", e);
        }
    }
}
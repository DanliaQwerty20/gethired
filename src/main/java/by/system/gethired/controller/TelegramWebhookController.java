package by.system.gethired.controller;

import by.system.gethired.controller.dto.TelegramUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/webhook")
public interface TelegramWebhookController {
    @PostMapping
    ResponseEntity<Void> onUpdate(@RequestBody TelegramUpdateDto update);
}
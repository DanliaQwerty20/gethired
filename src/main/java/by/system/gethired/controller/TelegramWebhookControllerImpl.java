package by.system.gethired.controller;

import by.system.gethired.controller.dto.TelegramUpdateDto;
import by.system.gethired.service.facade.TelegramFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TelegramWebhookControllerImpl implements TelegramWebhookController {
    private final TelegramFacade facade;

    @Override
    public ResponseEntity<Void> onUpdate(TelegramUpdateDto update) {
        facade.process(update);
        return ResponseEntity.ok().build();
    }
}

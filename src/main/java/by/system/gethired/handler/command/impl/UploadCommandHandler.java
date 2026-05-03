package by.system.gethired.handler.command.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.User;
import by.system.gethired.enums.UserState;
import by.system.gethired.handler.command.CommandHandler;
import by.system.gethired.service.user.UserService;
import by.system.gethired.util.StateComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class UploadCommandHandler implements CommandHandler {

    private final StateComponent stateComponent;
    private final TelegramClient telegramClient;
    private final UserService userService;

    @Override
    public boolean supports(String command) {
        return "/upload".equals(command);
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        User user = userService.getByChatId(chatId);

        stateComponent.setState(chatId, UserState.WAITING_FOR_RESUME_FILE);
        telegramClient.sendMessage(chatId,
                "📎 Загрузи резюме в формате PDF или DOCX.\n" +
                        "Если хочешь загрузить сопроводительное письмо, отправь файл с именем \"cover\" (будет реализовано позже)."
        );
    }
}

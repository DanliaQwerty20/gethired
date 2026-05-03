package by.system.gethired.handler.command.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.handler.command.CommandHandler;
import by.system.gethired.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

    private final UserService userService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(String command) {
        return "/start".equals(command);
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        String username = message.getFrom() != null ? message.getFrom().getUserName() : null;
        String firstName = message.getFrom() != null ? message.getFrom().getFirstName() : null;

        userService.register(chatId, username, firstName);
        telegramClient.sendMessage(chatId,
                "👋 Привет! Я — GetHiredBot, твой карьерный ассистент.\n\n" +
                        "Что я умею:\n" +
                        "• /settings — настроить фильтры поиска вакансий\n" +
                        "• /upload — загрузить резюме или пример сопроводительного письма\n" +
                        "• /vacancies — получить подходящие вакансии (по твоему фильтру)\n" +
                        "• /generate — сгенерировать резюме или письмо под конкретную вакансию\n\n" +
                        "Давай начнём с /settings, чтобы я понимал, что тебе нужно!"
        );
    }
}
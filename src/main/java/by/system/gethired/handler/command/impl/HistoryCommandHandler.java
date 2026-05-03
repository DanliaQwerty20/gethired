package by.system.gethired.handler.command.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.User;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.handler.command.CommandHandler;
import by.system.gethired.service.history.VacancyHistoryService;
import by.system.gethired.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoryCommandHandler implements CommandHandler {

    private final UserService userService;
    private final VacancyHistoryService historyService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(String command) {
        return "/history".equals(command);
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        User user = userService.getByChatId(chatId);
        List<Vacancy> recent = historyService.getRecentVacancies(user, 10);
        if (recent.isEmpty()) {
            telegramClient.sendMessage(chatId, "📭 История пуста. Сначала получи вакансии через /vacancies");
            return;
        }
        StringBuilder sb = new StringBuilder("📋 Последние вакансии:\n");
        for (int i = 0; i < recent.size(); i++) {
            Vacancy v = recent.get(i);
            sb.append(i + 1).append(". ").append(v.getTitle())
                    .append(" (").append(v.getLocation()).append(") — ").append(v.getSalary()).append("\n")
                    .append(v.getUrl()).append("\n\n");
        }
        telegramClient.sendMessage(chatId, sb.toString());
    }
}

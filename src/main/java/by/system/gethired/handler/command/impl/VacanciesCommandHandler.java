package by.system.gethired.handler.command.impl;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.User;
import by.system.gethired.entity.UserFilter;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.handler.command.CommandHandler;
import by.system.gethired.service.filter.FilterService;
import by.system.gethired.service.hh.HeadHunterService;
import by.system.gethired.service.matching.MatchingService;
import by.system.gethired.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VacanciesCommandHandler implements CommandHandler {

    private final UserService userService;
    private final FilterService filterService;
    private final HeadHunterService headHunterService;
    private final MatchingService matchingService;
    private final TelegramClient telegramClient;

    @Override
    public boolean supports(String command) {
        return "/vacancies".equals(command);
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        User user = userService.getByChatId(chatId);
        Optional<UserFilter> filterOpt = filterService.getFilter(user);

        if (filterOpt.isEmpty()) {
            telegramClient.sendMessage(chatId,
                    "❌ Сначала настрой фильтры через /settings"
            );
            return;
        }

        UserFilter filter = filterOpt.get();
        telegramClient.sendMessage(chatId, "🔎 Ищу подходящие вакансии...");

        List<Vacancy> vacancies = headHunterService.search(filter, 0, 20);
        matchingService.processNewVacancies(filter, vacancies);

        if (vacancies.isEmpty()) {
            telegramClient.sendMessage(chatId, "По заданным фильтрам ничего не найдено.");
        }
    }
}
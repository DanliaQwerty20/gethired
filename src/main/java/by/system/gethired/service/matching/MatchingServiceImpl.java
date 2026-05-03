package by.system.gethired.service.matching;

import by.system.gethired.client.TelegramClient;
import by.system.gethired.entity.UserFilter;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final VacancyRepository vacancyRepository;
    private final TelegramClient telegramClient;

    @Override
    @Transactional
    public void processNewVacancies(UserFilter filter, List<Vacancy> vacancies) {
        if (vacancies == null || vacancies.isEmpty()) return;

        List<Vacancy> newVacancies = new ArrayList<>();
        for (Vacancy v : vacancies) {
            if (!vacancyRepository.existsByExternalId(v.getExternalId())) {
                if (matchesKeywords(filter, v.getDescription()) && matchesSalary(filter, v.getSalary())) {
                    vacancyRepository.save(v);
                    newVacancies.add(v);
                }
            }
        }

        Long chatId = filter.getUser().getChatId();
        for (Vacancy v : newVacancies) {
            String message = formatVacancyMessage(v);
            telegramClient.sendMessage(chatId, message);
        }
    }

    private boolean matchesKeywords(UserFilter filter, String description) {
        if (description == null) return true;
        String text = description.toLowerCase();

        // include keywords
        if (filter.getIncludeKeywords() != null && !filter.getIncludeKeywords().isBlank()) {
            String[] includes = filter.getIncludeKeywords().toLowerCase().split(",\\s*");
            for (String inc : includes) {
                if (!text.contains(inc.trim())) return false;
            }
        }

        // exclude keywords
        if (filter.getExcludeKeywords() != null && !filter.getExcludeKeywords().isBlank()) {
            String[] excludes = filter.getExcludeKeywords().toLowerCase().split(",\\s*");
            for (String exc : excludes) {
                if (text.contains(exc.trim())) return false;
            }
        }
        return true;
    }

    private boolean matchesSalary(UserFilter filter, String salaryText) {
        // TODO Заглушка salaryText
        return true;
    }

    private String formatVacancyMessage(Vacancy v) {
        return String.format("""
                🔔 **%s**
                📍 %s
                💰 %s
                📝 %s
                %s
                """, v.getTitle(), v.getLocation(), v.getSalary(), v.getDescription(), v.getUrl());
    }
}
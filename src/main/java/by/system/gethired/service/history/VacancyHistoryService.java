package by.system.gethired.service.history;

import by.system.gethired.entity.User;
import by.system.gethired.entity.Vacancy;

import java.util.List;

public interface VacancyHistoryService {
    void recordView(User user, Vacancy vacancy);
    List<Vacancy> getRecentVacancies(User user, int limit);
}

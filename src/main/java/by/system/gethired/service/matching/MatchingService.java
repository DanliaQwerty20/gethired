package by.system.gethired.service.matching;

import by.system.gethired.entity.UserFilter;
import by.system.gethired.entity.Vacancy;

import java.util.List;

public interface MatchingService {

    void processNewVacancies(UserFilter filter, List<Vacancy> vacancies);
}
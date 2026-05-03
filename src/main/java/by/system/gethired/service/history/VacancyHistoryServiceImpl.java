package by.system.gethired.service.history;

import by.system.gethired.entity.User;
import by.system.gethired.entity.UserVacancyView;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.repository.UserVacancyViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyHistoryServiceImpl implements VacancyHistoryService {

    private final UserVacancyViewRepository viewRepository;

    @Transactional
    @Override
    public void recordView(User user, Vacancy vacancy) {
        UserVacancyView view = new UserVacancyView();
        view.setUser(user);
        view.setVacancy(vacancy);
        viewRepository.save(view);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Vacancy> getRecentVacancies(User user, int limit) {
        return viewRepository.findTop10ByUser_IdOrderByViewedAtDesc(user.getId())
                .stream()
                .map(UserVacancyView::getVacancy)
                .toList();
    }
}

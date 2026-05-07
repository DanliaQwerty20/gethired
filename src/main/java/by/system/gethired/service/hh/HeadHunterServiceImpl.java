package by.system.gethired.service.hh;

import by.system.gethired.entity.UserFilter;
import by.system.gethired.entity.Vacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeadHunterServiceImpl implements HeadHunterService {

    private final HhVacancyParser parser;

    @Override
    public List<Vacancy> search(UserFilter filter, int page, int perPage) {
        return parser.fetchVacancies(filter, 5, perPage);
    }
}
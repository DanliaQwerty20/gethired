package by.system.gethired.service.hh;


import by.system.gethired.entity.UserFilter;
import by.system.gethired.entity.Vacancy;

import java.util.List;

public interface HeadHunterService {

    List<Vacancy> search(UserFilter filter, int page, int perPage);
}
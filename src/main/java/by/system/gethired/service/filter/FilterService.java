package by.system.gethired.service.filter;

import by.system.gethired.entity.User;
import by.system.gethired.entity.UserFilter;

import java.util.Optional;

public interface FilterService {

    Optional<UserFilter> getFilter(User user);

    UserFilter saveFilter(User user, UserFilter filter);

    void updateJobTitle(User user, String jobTitle);

    void updateLocation(User user, String location);

    void updateSalaryFrom(User user, Integer salaryFrom);

    void updateSalaryTo(User user, Integer salaryTo);

    void updateIncludeKeywords(User user, String keywords);

    void updateExcludeKeywords(User user, String keywords);
}
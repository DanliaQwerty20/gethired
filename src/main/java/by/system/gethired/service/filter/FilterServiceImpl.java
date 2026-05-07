package by.system.gethired.service.filter;

import by.system.gethired.entity.User;
import by.system.gethired.entity.UserFilter;
import by.system.gethired.repository.UserFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {
    private final UserFilterRepository filterRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<UserFilter> getFilter(User user) {
        return filterRepository.findWithUser(user.getId());
    }

    @Transactional
    @Override
    public UserFilter saveFilter(User user, UserFilter filter) {
        filter.setUser(user);
        return filterRepository.save(filter);
    }

    @Transactional
    @Override
    public void updateJobTitle(User user, String jobTitle) {
        UserFilter filter = filterRepository.findWithUser(user.getId())
                .orElseGet(() -> UserFilter.create(user));
        filter.setJobTitle(jobTitle);
        filterRepository.save(filter);
    }

    @Transactional
    @Override
    public void updateLocation(User user, String location) {
        UserFilter filter = filterRepository.findWithUser(user.getId())
                .orElseGet(() -> UserFilter.create(user));
        filter.setLocation(location);
        filterRepository.save(filter);
    }

    @Transactional
    @Override
    public void updateSalaryFrom(User user, Integer salaryFrom) {
        UserFilter filter = filterRepository.findWithUser(user.getId())
                .orElseGet(() -> UserFilter.create(user));
        filter.setSalaryFrom(salaryFrom);
        filterRepository.save(filter);
    }

    @Transactional
    @Override
    public void updateSalaryTo(User user, Integer salaryTo) {
        UserFilter filter = filterRepository.findWithUser(user.getId())
                .orElseGet(() -> UserFilter.create(user));
        filter.setSalaryTo(salaryTo);
        filterRepository.save(filter);
    }

    @Transactional
    @Override
    public void updateIncludeKeywords(User user, String keywords) {
        UserFilter filter = filterRepository.findWithUser(user.getId())
                .orElseGet(() -> UserFilter.create(user));
        filter.setIncludeKeywords(keywords);
        filterRepository.save(filter);
    }

    @Transactional
    @Override
    public void updateExcludeKeywords(User user, String keywords) {
        UserFilter filter = filterRepository.findWithUser(user.getId())
                .orElseGet(() -> UserFilter.create(user));
        filter.setExcludeKeywords(keywords);
        filterRepository.save(filter);
    }
}
package by.system.gethired.scheduler;

import by.system.gethired.entity.UserFilter;
import by.system.gethired.entity.Vacancy;
import by.system.gethired.repository.UserFilterRepository;
import by.system.gethired.service.hh.HeadHunterService;
import by.system.gethired.service.matching.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCrawlerScheduler {

    private final UserFilterRepository filterRepository;
    private final HeadHunterService headHunterService;
    private final MatchingService matchingService;

    @Scheduled(cron = "${app.crawler.cron}")
    public void crawlVacancies() {
        log.info("Starting scheduled vacancy crawl...");
        List<UserFilter> filters = filterRepository.findAll();
        for (UserFilter filter : filters) {
            try {
                List<Vacancy> vacancies = headHunterService.search(filter, 0, 100);
                matchingService.processNewVacancies(filter, vacancies);
            } catch (Exception e) {
                log.error("Error processing filter for user {}", filter.getUser().getChatId(), e);
            }
        }
        log.info("Vacancy crawl completed.");
    }
}
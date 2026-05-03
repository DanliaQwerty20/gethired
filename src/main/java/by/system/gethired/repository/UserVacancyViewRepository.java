package by.system.gethired.repository;

import by.system.gethired.entity.UserVacancyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserVacancyViewRepository extends JpaRepository<UserVacancyView, UUID> {
    List<UserVacancyView> findTop10ByUser_IdOrderByViewedAtDesc(UUID userId);
}

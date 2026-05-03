package by.system.gethired.repository;

import by.system.gethired.entity.UserFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFilterRepository extends JpaRepository<UserFilter, UUID> {
    Optional<UserFilter> findByUser_Id(UUID userId);
}
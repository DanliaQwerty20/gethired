package by.system.gethired.repository;

import by.system.gethired.entity.UserFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFilterRepository extends JpaRepository<UserFilter, UUID> {

    @Query("""
                select f
                from UserFilter f
                join fetch f.user
                where f.user.id = :userId
            """)
    Optional<UserFilter> findWithUser(UUID userId);
}
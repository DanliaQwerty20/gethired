package by.system.gethired.repository;

import by.system.gethired.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    boolean existsByExternalId(Long externalId);

    @Query(value = """
            SELECT * FROM vacancies
            WHERE embedding IS NOT NULL
            ORDER BY embedding <=> CAST(:queryEmbedding AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<Vacancy> findSimilarVacancies(
            @Param("queryEmbedding") String queryEmbedding,
            @Param("limit") int limit);
}
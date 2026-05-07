package by.system.gethired.service.embedding;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyEmbeddingServiceImpl implements VacancyEmbeddingService {

    private final EmbeddingClient embeddingClient;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public void saveEmbedding(Long externalId, String text) {
        if (text == null || text.isBlank()) return;
        List<Double> vector = embeddingClient.embed(text);
        String literal = vector.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "[", "]"));
        jdbcTemplate.update(
                "UPDATE vacancies SET embedding = ?::vector WHERE external_id = ?",
                literal, externalId
        );
    }

    @Override
    public List<Long> findSimilarVacancyIds(List<Double> queryVector, int limit) {
        String literal = queryVector.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "[", "]"));
        return jdbcTemplate.queryForList(
                "SELECT external_id FROM vacancies WHERE embedding IS NOT NULL ORDER BY embedding <=> ?::vector LIMIT ?",
                Long.class, literal, limit
        );
    }
}
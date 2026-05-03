package by.system.gethired.service.embedding;

import by.system.gethired.entity.Vacancy;
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
    public void generateAndSaveEmbedding(Vacancy vacancy) {
        String text = (vacancy.getTitle() != null ? vacancy.getTitle() : "") + " "
                + (vacancy.getDescription() != null ? vacancy.getDescription() : "");

        if (text.isBlank()) return;

        List<List<Double>> embeddings = embeddingClient.embed(List.of(text));
        List<Double> vector = embeddings.get(0);  // первый элемент

        String vectorLiteral = vector.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "[", "]"));

        jdbcTemplate.update(
                "UPDATE vacancies SET embedding = ?::vector WHERE external_id = ?",
                vectorLiteral, vacancy.getExternalId()
        );
    }
}

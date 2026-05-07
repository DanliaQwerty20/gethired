package by.system.gethired.service.embedding;

import java.util.List;

public interface VacancyEmbeddingService {
    void saveEmbedding(Long externalId, String text);
    List<Long> findSimilarVacancyIds(List<Double> queryVector, int limit);
}

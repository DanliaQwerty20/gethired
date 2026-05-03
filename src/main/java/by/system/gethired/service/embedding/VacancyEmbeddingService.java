package by.system.gethired.service.embedding;

import by.system.gethired.entity.Vacancy;

public interface VacancyEmbeddingService {
    void generateAndSaveEmbedding(Vacancy vacancy);
}

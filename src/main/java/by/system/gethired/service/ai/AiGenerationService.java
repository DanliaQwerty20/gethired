package by.system.gethired.service.ai;

import by.system.gethired.entity.Vacancy;

public interface AiGenerationService {

    String generateResume(Long chatId, Vacancy vacancy);

    String generateCoverLetter(Long chatId, Vacancy vacancy);
}
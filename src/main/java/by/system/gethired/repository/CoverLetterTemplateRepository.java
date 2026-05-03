package by.system.gethired.repository;

import by.system.gethired.entity.CoverLetterTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoverLetterTemplateRepository extends JpaRepository<CoverLetterTemplate, UUID> {
    Optional<CoverLetterTemplate> findTopByUser_Id(UUID userId);
}
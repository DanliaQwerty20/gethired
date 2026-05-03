package by.system.gethired.repository;

import by.system.gethired.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    List<Resume> findByUser_IdOrderByUploadedAtDesc(UUID userId);
    Optional<Resume> findTopByUser_IdOrderByUploadedAtDesc(UUID userId);
}
package by.system.gethired.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vacancies",
        indexes = {
                @Index(name = "idx_vacancies_published_at", columnList = "published_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Vacancy {

    @Id
    @Column(name = "external_id", nullable = false)
    private Long externalId;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "salary", length = 255)
    private String salary;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "url", length = 1024)
    private String url;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "embedding", columnDefinition = "vector(768)")
    private float[] embedding;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
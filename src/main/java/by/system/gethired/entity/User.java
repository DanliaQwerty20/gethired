package by.system.gethired.entity;

import by.system.gethired.enums.UserState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_chat_id", columnList = "chat_id", unique = true)
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "username", length = 255)
    private String username;

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 50)
    private UserState state = UserState.IDLE;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static User create(Long chatId, String username, String firstName) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.chatId = chatId;
        user.username = username;
        user.firstName = firstName;
        user.state = UserState.IDLE;
        return user;
    }
}
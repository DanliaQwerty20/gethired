package by.system.gethired.service.user;

import by.system.gethired.entity.User;
import by.system.gethired.enums.UserState;
import by.system.gethired.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User register(Long chatId, String username, String firstName) {
        return userRepository.findByChatId(chatId)
                .orElseGet(() -> {
                    User newUser = User.create(chatId, username, firstName);
                    return userRepository.save(newUser);
                });
    }

    @Transactional(readOnly = true)
    @Override
    public User getByChatId(Long chatId) {
        return userRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by chatId: " + chatId));
    }

    @Transactional
    @Override
    public void updateState(User user, UserState state) {
        user.setState(state);
        userRepository.save(user);
    }
}
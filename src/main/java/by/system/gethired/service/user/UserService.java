package by.system.gethired.service.user;


import by.system.gethired.entity.User;
import by.system.gethired.enums.UserState;

public interface UserService {

    User register(Long chatId, String username, String firstName);

    User getByChatId(Long chatId);

    void updateState(User user, UserState state);
}
package by.system.gethired.util;

import by.system.gethired.enums.UserState;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StateComponent {
    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();

    public UserState getState(Long chatId) {
        return userStates.getOrDefault(chatId, UserState.IDLE);
    }

    public void setState(Long chatId, UserState state) {
        userStates.put(chatId, state);
    }

    public void clear(Long chatId) {
        userStates.remove(chatId);
    }
}
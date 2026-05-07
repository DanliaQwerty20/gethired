package by.system.gethired.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthControllerImpl implements OAuthController {
    @Override
    public ResponseEntity<String> hhCallback(String code, String state) {
        System.out.println("Получен code: " + code);
        System.out.println("state: " + state);
        return ResponseEntity.ok("Авторизация hh.ru выполнена. Можете закрыть это окно.");
    }
}

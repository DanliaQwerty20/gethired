package by.system.gethired.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(("/oauth/callback"))
public interface OAuthController {

    @GetMapping
    ResponseEntity<String> hhCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state
    );
}
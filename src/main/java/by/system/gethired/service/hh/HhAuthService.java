package by.system.gethired.service.hh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class HhAuthService {

    private final String clientId;
    private final String clientSecret;
    private final RestTemplate restTemplate;
    private final TokenStorage tokenStorage;

    private String accessToken;

    public HhAuthService(
            @Value("${hh.client-id}") String clientId,
            @Value("${hh.client-secret}") String clientSecret,
            TokenStorage tokenStorage
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenStorage = tokenStorage;
        this.restTemplate = new RestTemplate();

        TokenStorage.TokenData saved = tokenStorage.load();

        if (saved != null) {
            this.accessToken = saved.accessToken();
        }
    }

    public synchronized String getAccessToken() {
        if (accessToken == null || accessToken.isBlank()) {
            refreshToken();
        }

        return accessToken;
    }

    private void refreshToken() {

        String url = "https://api.hh.ru/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("HH-User-Agent", "GetHiredBot/1.0");

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null) {
            throw new RuntimeException("HH token response is empty");
        }

        accessToken = (String) responseBody.get("access_token");

        tokenStorage.save(
                new TokenStorage.TokenData(accessToken)
        );
    }
}
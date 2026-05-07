package by.system.gethired.client;

import by.system.gethired.exception.CaptchaRequiredException;
import by.system.gethired.service.hh.HhAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class HhApiClient {

    private final RestClient restClient;
    private final HhAuthService authService;
    private final long delayMs;
    private final int maxRetries;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public HhApiClient(RestClient restClient, HhAuthService authService,
                       long delayMs, int maxRetries) {
        this.restClient = restClient;
        this.authService = authService;
        this.delayMs = delayMs;
        this.maxRetries = maxRetries;
    }

    /**
     * Выполнить GET‑запрос с повторами, троттлингом и автодобавлением токена.
     */
    public <T> T get(String url, Class<T> responseType, Map<String, Object> params) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // троттлинг перед каждым запросом
                sleep(delayMs);
                String token = authService.getAccessToken();
                T result = restClient.get()
                        .uri(uriBuilder -> {
                            var builder = uriBuilder.path(url);
                            if (params != null) {
                                params.forEach(builder::queryParam);
                            }
                            return builder.build();
                        })
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError, (req, resp) -> {
                            throw new HttpServerErrorException(resp.getStatusCode(), resp.getStatusText());
                        })
                        .body(responseType);
                return result;
            } catch (HttpClientErrorException.Forbidden e) {
                // проверка на капчу
                String body = e.getResponseBodyAsString();
                if (body != null && body.contains("captcha_required")) {
                    log.error("Captcha required by HH API");
                    throw new CaptchaRequiredException("Captcha required", e);
                }
                // другие ошибки клиента – повторяем, если разрешено
                if (attempt == maxRetries) throw e;
                log.warn("Forbidden, retry {}/{}: {}", attempt, maxRetries, e.getMessage());
            } catch (HttpServerErrorException e) {
                if (attempt == maxRetries) throw e;
                log.warn("Server error, retry {}/{}: {}", attempt, maxRetries, e.getMessage());
            } catch (ResourceAccessException e) {
                if (attempt == maxRetries) throw e;
                log.warn("IO error, retry {}/{}: {}", attempt, maxRetries, e.getMessage());
            }
            // перед повтором увеличиваем паузу
            sleep(delayMs * attempt);
        }
        throw new RuntimeException("Unreachable");
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
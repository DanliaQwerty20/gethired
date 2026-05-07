package by.system.gethired.config;

import by.system.gethired.client.HhApiClient;
import by.system.gethired.service.hh.HhAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaAuditing
public class AppConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://api.hh.ru")
                .defaultHeader("User-Agent", "GetHiredBot/1.0 (danlia2000789@gmail.com)")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    public HhApiClient hhApiClient(RestClient restClient, HhAuthService authService) {
        return new HhApiClient(restClient, authService, 1000, 3); // 1 запрос/сек, 3 повтора
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
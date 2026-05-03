package by.system.gethired.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestClient;

@Configuration
@EnableJpaAuditing
public class AppConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultHeader("User-Agent", "GetHiredBot/1.0 (danlia2000789@gmail.com)")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
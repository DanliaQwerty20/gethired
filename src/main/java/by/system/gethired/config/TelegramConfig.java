package by.system.gethired.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.telegram.telegrambots.bots.DefaultBotOptions;


import org.telegram.telegrambots.bots.DefaultAbsSender;

@Configuration
public class TelegramConfig {

    @Value("${telegram.bot.token}")
    private String token;

    @Bean
    public DefaultAbsSender defaultAbsSender() {
        return new DefaultAbsSender(new DefaultBotOptions(), token) {
        };
    }
}
package com.example.pharma.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    @Bean
    @Qualifier("openAiWebClient")
    public WebClient openAiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.freemodel.dev")
                .defaultHeader("Authorization", "Bearer " + "fe_oa_1205c6850641a08b91cdf54caed416dfb5197cf5f3cb9a3f")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    @Qualifier("geminiWebClient")
    public WebClient geminiWebClient() {
        return WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
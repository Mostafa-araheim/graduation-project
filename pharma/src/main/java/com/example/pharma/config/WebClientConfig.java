package com.example.pharma.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {


    private String openAiApiKey;
//    @Bean
//    public WebClient aiWebClient() {
//
//        HttpClient httpClient = HttpClient.create()
//                .responseTimeout(Duration.ofSeconds(30));
//
//        return WebClient.builder()
//                .baseUrl("https://dvpf7gmr-8000.euw.devtunnels.ms/")
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .build();
//    }
    @Bean
    public WebClient aiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.freemodel.dev")
                .defaultHeader("Authorization", "Bearer " + "fe_oa_49fac72547186cef961f95bdde685315369e8374b95851ed")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}

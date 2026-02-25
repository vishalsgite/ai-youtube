package com.vishal.aiyoutube.youtube_processing_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Random;

@Configuration
public class YoutubeConfig {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.base-url}")
    private String baseUrl;

    // Fixed path as defined in your docker-compose volumes
    private static final String COOKIE_PATH = "/app/youtube_cookies.txt";

    private static final List<String> USER_AGENTS = List.of(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0"
    );

    private final Random random = new Random();

    @Bean
    public WebClient youtubeWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", getRandomUserAgent())
                .build();
    }

    public String getRandomUserAgent() {
        return USER_AGENTS.get(random.nextInt(USER_AGENTS.size()));
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getCookiePath() {
        return COOKIE_PATH;
    }
}
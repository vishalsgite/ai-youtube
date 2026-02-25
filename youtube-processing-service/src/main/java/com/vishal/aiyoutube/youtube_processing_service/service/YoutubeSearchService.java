package com.vishal.aiyoutube.youtube_processing_service.service;

import com.vishal.aiyoutube.youtube_processing_service.config.YoutubeConfig;
import com.vishal.aiyoutube.youtube_processing_service.dto.YoutubeSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Implementation of the YouTube Search Service using Spring WebClient.
 * This service acts as the 'Discovery Agent' in your multi-agent system.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeSearchService implements IYouTubeSearchService {

    private final WebClient youtubeWebClient;
    private final YoutubeConfig youtubeConfig;

    /**
     * Executes an asynchronous search request to YouTube v3 API.
     * * KEY ARCHITECTURAL FEATURES:
     * 1. Non-Blocking I/O: Uses WebClient to ensure the service doesn't idle
     * while waiting for YouTube's response.
     * 2. Query Parameters: Specifically filters for 'video' types to ensure
     * we only fetch content that likely contains transcripts.
     */
    @Override
    public Mono<YoutubeSearchResponse> searchVideos(String query, int maxResults) {
        log.info("Initiating YouTube search for: [{}]", query);

        return youtubeWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("part", "snippet")
                        .queryParam("q", query)
                        .queryParam("type", "video")
                        .queryParam("maxResults", maxResults)
                        .queryParam("key", youtubeConfig.getApiKey())
                        .build())
                .retrieve()
                /**
                 * Error Handling:
                 * Maps 4xx/5xx errors into meaningful logs to prevent
                 * silent failures in the distributed pipeline.
                 */
                .onStatus(status -> status.isError(), response -> {
                    log.error("YouTube API error: {}", response.statusCode());
                    return Mono.error(new RuntimeException("YouTube API Search Failed"));
                })
                .bodyToMono(YoutubeSearchResponse.class)
                .doOnSuccess(response -> log.info("Successfully discovered {} video sources.",
                        response.getItems() != null ? response.getItems().size() : 0));
    }
}
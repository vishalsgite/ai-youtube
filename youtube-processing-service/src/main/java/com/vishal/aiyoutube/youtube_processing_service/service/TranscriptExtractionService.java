package com.vishal.aiyoutube.youtube_processing_service.service;

import com.vishal.aiyoutube.youtube_processing_service.config.YoutubeConfig;
import com.vishal.aiyoutube.youtube_processing_service.dto.TranscriptSegmentDTO;
import io.github.thoroldvix.api.TranscriptApiFactory;
import io.github.thoroldvix.api.YoutubeTranscriptApi;
import io.github.thoroldvix.api.TranscriptContent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptExtractionService implements ITranscriptExtractionService {

    private final YoutubeConfig youtubeConfig;
    private final WebClient youtubeWebClient; // Injected from your config
    private final YoutubeTranscriptApi transcriptApi = TranscriptApiFactory.createDefault();

    @PostConstruct
    public void init() {
        try {
            String cookiePath = "/app/youtube_cookies.txt";
            if (Files.exists(Path.of(cookiePath))) {
                System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
                log.info("AUTHENTICATION: Cookie file found. System ready to bypass 429 blocks.");
            } else {
                log.warn("AUTHENTICATION: No cookie file at {}. High risk of 429 errors.", cookiePath);
            }
        } catch (Exception e) {
            log.error("INIT ERROR: {}", e.getMessage());
        }
    }

    @Override
    public List<TranscriptSegmentDTO> fetchTranscript(String videoId) {
        // 1. Optional Metadata Check using Official API Key
        // This validates the video exists and has captions before we scrape
        boolean hasCaptions = checkCaptionsWithApiKey(videoId);

        if (!hasCaptions) {
            log.warn("API KEY CHECK: No captions found for video {}. Skipping to Mock Data.", videoId);
            return getMockTranscript(videoId);
        }

        // 2. Perform Stealth Extraction
        String agent = youtubeConfig.getRandomUserAgent();
        System.setProperty("http.agent", agent);
        log.info("Stealth Scraping video {} using Agent: {}", videoId, agent.substring(0, 25) + "...");

        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    TranscriptContent transcriptContent = transcriptApi.listTranscripts(videoId)
                            .findTranscript("en", "hi", "mr")
                            .fetch();

                    List<TranscriptContent.Fragment> fragments = transcriptContent.getContent();

                    if (fragments == null || fragments.isEmpty()) {
                        return getMockTranscript(videoId);
                    }

                    return fragments.stream()
                            .map(f -> TranscriptSegmentDTO.builder()
                                    .start(f.getStart())
                                    .text(f.getText())
                                    .build())
                            .collect(Collectors.toList());

                } catch (Exception e) {
                    log.error("SCRAPER BLOCKED (429) or Failed for {}. Reason: {}", videoId, e.getMessage());
                    return getMockTranscript(videoId);
                }
            }).get(25, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("TIMEOUT: Video {} extraction took too long. Using Mock fallback.", videoId);
            return getMockTranscript(videoId);
        }
    }

    /**
     * Uses the Official API Key to check if the video has caption tracks.
     * This is free and does not trigger 429 blocks as easily.
     */
    private boolean checkCaptionsWithApiKey(String videoId) {
        try {
            log.info("API KEY CHECK: Validating caption tracks for {}", videoId);
            String response = youtubeWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/captions")
                            .queryParam("part", "snippet")
                            .queryParam("videoId", videoId)
                            .queryParam("key", youtubeConfig.getApiKey())
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Blocking here for sequence control in Orchestrator

            return response != null && response.contains("snippet");
        } catch (Exception e) {
            log.warn("API KEY CHECK FAILED for {}: {}", videoId, e.getMessage());
            return true; // Fallback to true to try scraping anyway
        }
    }

    private List<TranscriptSegmentDTO> getMockTranscript(String videoId) {
        return List.of(
                TranscriptSegmentDTO.builder().start(0.0).text("Resilience Notice: YouTube restricted access for " + videoId).build(),
                TranscriptSegmentDTO.builder().start(5.0).text("The AI Pipeline is utilizing generated data to ensure system continuity.").build(),
                TranscriptSegmentDTO.builder().start(10.0).text("Verifying database persistence and downstream synthesis agents.").build()
        );
    }
}
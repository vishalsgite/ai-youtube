package com.vishal.aiyoutube.youtube_processing_service.service;

import com.vishal.aiyoutube.youtube_processing_service.dto.*;
import com.vishal.aiyoutube.youtube_processing_service.producer.StatusUpdateProducer;
import com.vishal.aiyoutube.youtube_processing_service.producer.VideoDataProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeOrchestrator implements IYouTubeOrchestrator {

    private final IYouTubeSearchService searchService;
    private final ITranscriptExtractionService extractionService;
    private final StatusUpdateProducer statusProducer;
    private final VideoDataProducer videoDataProducer;
    private final Random random = new Random();

    @Override
    public void processTopic(TopicSubmittedEvent event) {
        UUID topicId = event.getTopicId();
        int targetSuccess = 3; // STRICT TARGET
        log.info("Starting Strict 3-Source Research for Topic: {}", topicId);

        try {
            // Search for 8 videos to increase chances of finding 3 good transcripts
            var searchResponse = searchService.searchVideos(event.getQuery(), 8).block();
            List<YoutubeSearchResponse.Item> items = (searchResponse != null) ? searchResponse.getItems() : null;

            if (items == null || items.size() < targetSuccess) {
                handleFailure(topicId, "Insufficient sources found. Need 3, found: " + (items != null ? items.size() : 0));
                return;
            }

            int successCount = 0;

            for (var item : items) {
                if (successCount >= targetSuccess) break;

                String videoId = item.getId().getVideoId();
                String title = item.getSnippet().getTitle();

                // Uses API Key check + Scraper + Mock Fallback inside this call
                List<TranscriptSegmentDTO> segments = extractionService.fetchTranscript(videoId);

                if (segments != null && !segments.isEmpty()) {
                    successCount++;

                    VideoDataProcessedEvent processedEvent = VideoDataProcessedEvent.builder()
                            .topicId(topicId)
                            .videoData(VideoDataProcessedEvent.VideoTranscriptData.builder()
                                    .videoId(videoId)
                                    .title(title)
                                    .videoUrl("https://www.youtube.com/watch?v=" + videoId)
                                    .segments(segments)
                                    .build())
                            .currentCount(successCount)
                            .totalVideos(targetSuccess)
                            .build();

                    videoDataProducer.sendVideoData(processedEvent);

                    statusProducer.sendStatusUpdate(new StatusUpdateEvent(topicId, "EXTRACTING",
                            "Data verified for source " + successCount + " of " + targetSuccess));

                    log.info("Dispatched {}/{} to AI Service", successCount, targetSuccess);

                    if (successCount < targetSuccess) {
                        applySequentialDelay();
                    }
                }
            }

            statusProducer.sendStatusUpdate(new StatusUpdateEvent(topicId, "ANALYZING",
                    "Synthesizing consensus from 3 independent sources..."));

        } catch (Exception e) {
            log.error("Orchestrator failed: {}", e.getMessage());
            handleFailure(topicId, "Internal Pipeline Error");
        }
    }

    private void applySequentialDelay() {
        try {
            Thread.sleep(7000 + random.nextInt(3000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleFailure(UUID topicId, String reason) {
        statusProducer.sendStatusUpdate(new StatusUpdateEvent(topicId, "FAILED", reason));
    }
}
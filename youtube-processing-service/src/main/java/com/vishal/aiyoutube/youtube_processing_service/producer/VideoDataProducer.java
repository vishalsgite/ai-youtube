package com.vishal.aiyoutube.youtube_processing_service.producer;

import com.vishal.aiyoutube.youtube_processing_service.dto.VideoDataProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Producer service responsible for streaming processed video intelligence.
 * It transmits heavy transcript data to the AI Analysis Service for
 * consensus and sentiment extraction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoDataProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * The Kafka topic dedicated to carrying processed video metadata and transcripts.
     */
    private static final String TOPIC_NAME = "video-data-processed-events";

    /**
     * Publishes a single video chunk to the AI Analysis Service for real-time processing.
     * * @param event The payload containing a single video's metadata and transcript segments.
     * * KEY ARCHITECTURAL FEATURES:
     * 1. Streaming Logic: Instead of batching, it streams videos individually to
     * enable "On-the-Spot" AI synthesis.
     * 2. Partition Affinity: Uses the Topic ID as the Kafka key to ensure all
     * chunks for a specific research query stay in the same partition,
     * guaranteeing the order of processing.
     */
    public void sendVideoData(VideoDataProcessedEvent event) {
        // Log the streaming progress (e.g., "Streaming video 2/5")
        log.info("Streaming video {}/{} for Topic ID: {}",
                event.getCurrentCount(),
                event.getTotalVideos(),
                event.getTopicId());

        /**
         * Asynchronous Kafka Transmission:
         * Uses the Topic ID as the key to preserve message ordering across the cluster.
         */
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(TOPIC_NAME, event.getTopicId().toString(), event);

        /**
         * Non-blocking Callback Handling:
         * Verifies the successful delivery of the 'Raw Intelligence' data.
         */
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent video chunk {} for Topic ID: [{}] at offset: [{}]",
                        event.getCurrentCount(),
                        event.getTopicId(),
                        result.getRecordMetadata().offset());
            } else {
                /**
                 * Error Management:
                 * Logs critical failures, such as network partitions or serialization
                 * errors, to prevent silent data loss in the distributed pipeline.
                 */
                log.error("CRITICAL: Unable to send video chunk for Topic ID: [{}] due to: {}",
                        event.getTopicId(), ex.getMessage());
            }
        });
    }
}
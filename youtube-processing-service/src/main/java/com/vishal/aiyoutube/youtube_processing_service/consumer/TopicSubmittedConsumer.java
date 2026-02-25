package com.vishal.aiyoutube.youtube_processing_service.consumer;

import com.vishal.aiyoutube.youtube_processing_service.dto.TopicSubmittedEvent;
import com.vishal.aiyoutube.youtube_processing_service.service.YouTubeOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * The Consumer component responsible for ingesting events from Kafka.
 * It serves as the 'Front Controller' for the processing logic in Service 2.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TopicSubmittedConsumer {

    private final YouTubeOrchestrator youtubeOrchestrator;

    /**
     * Entry point for Service 2.
     * * @KafkaListener: Subscribes to the 'topic-submitted-events' topic.
     * When a user submits a query like "Budget 2026 for middle class" in Service 1,
     * this method is automatically triggered.
     * * KEY CONCEPTS:
     * - GroupId: "youtube-processing-group" ensures load balancing across instances.
     * - ContainerFactory: Uses the custom factory to handle cross-package class mapping.
     */
    @KafkaListener(
            topics = "topic-submitted-events",
            groupId = "youtube-processing-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTopicSubmitted(TopicSubmittedEvent event) {
        // Log receipt of the event for traceability in the distributed pipeline
        log.info("Received TopicSubmittedEvent for Topic ID: {} with query: {}",
                event.getTopicId(), event.getQuery());

        try {
            /**
             * Trigger the orchestration logic (Search -> Extract -> Produce).
             * This hand-off initiates the sequence where YouTube data is gathered,
             * processed, and then sent to Llama-3 & Grok AI models for synthesis.
             */
            youtubeOrchestrator.processTopic(event);

            log.info("Successfully initiated processing for Topic ID: {}", event.getTopicId());
        } catch (Exception e) {
            /**
             * Error Handling:
             * Prevents the consumer from crashing and allows for fault-tolerant
             * reporting back to the Topic Management service.
             */
            log.error("Failed to process YouTube data for Topic ID: {}. Error: {}",
                    event.getTopicId(), e.getMessage(), e);
        }
    }
}
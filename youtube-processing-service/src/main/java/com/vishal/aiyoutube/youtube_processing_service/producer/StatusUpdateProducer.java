package com.vishal.aiyoutube.youtube_processing_service.producer;

import com.vishal.aiyoutube.youtube_processing_service.dto.StatusUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Producer service responsible for transmitting pipeline state changes.
 * It provides real-time observability into the multi-agent intelligence process.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatusUpdateProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Dedicated Kafka topic for tracking microservice progress.
     */
    private static final String TOPIC_NAME = "pipeline-status-updates";

    /**
     * Publishes a status update to the Topic Management Service.
     * * @param event The status payload containing Topic ID, Status, and Message.
     * * KEY ARCHITECTURAL FEATURES:
     * 1. Message Ordering: Uses topicId as the Kafka Partition Key to ensure
     * status events (e.g., SEARCHING -> EXTRACTING) are processed in order.
     * 2. Asynchronous Execution: Uses CompletableFuture to prevent blocking
     * the main processing thread during network transmission.
     */
    public void sendStatusUpdate(StatusUpdateEvent event) {
        log.info("Publishing status update [{}] for Topic ID: {}", event.getStatus(), event.getTopicId());

        try {
            // Initiate asynchronous send to the Kafka cluster
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(TOPIC_NAME, event.getTopicId().toString(), event);

            /**
             * Exception & Callback Handling:
             * Monitors the success or failure of the Kafka transmission.
             */
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Status update successfully published for Topic ID: {}", event.getTopicId());
                } else {
                    /**
                     * Handle Transmission Exceptions:
                     * Logs failures if the Kafka broker is unreachable or the
                     * serialization fails, ensuring the orchestrator is aware.
                     */
                    log.error("Failed to publish status update for Topic ID: {}: {}",
                            event.getTopicId(), ex.getMessage());
                }
            });
        } catch (Exception e) {
            /**
             * Handle Runtime Exceptions:
             * Catches immediate issues like template misconfiguration or
             * thread interruption before the message even leaves the service.
             */
            log.error("Critical error in StatusUpdateProducer for Topic ID: {}: {}",
                    event.getTopicId(), e.getMessage());
        }
    }
}
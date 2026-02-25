package com.vishal.aiyoutube.youtube_processing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for pipeline progress tracking.
 * This event is published to the 'pipeline-status-updates' Kafka topic
 * to provide real-time feedback to the end-user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateEvent {

    /**
     * Unique identifier for the research topic.
     * Links the status update to a specific user request (e.g., Budget 2026).
     */
    private UUID topicId;

    /**
     * The current state of the pipeline.
     * Common values: 'SEARCHING', 'EXTRACTING', 'ANALYZING', 'COMPLETED', 'FAILED'.
     */
    private String status;

    /**
     * A human-readable description of the current task.
     * Example: "Agents gathering YouTube transcripts..." or "Llama-3 performing synthesis..."
     */
    private String message;
}
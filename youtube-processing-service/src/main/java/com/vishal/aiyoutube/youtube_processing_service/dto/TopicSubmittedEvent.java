package com.vishal.aiyoutube.youtube_processing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a new research request.
 * This event is published by the Topic Management Service and consumed
 * by the YouTube Processing Service to initiate data gathering.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicSubmittedEvent {

    /**
     * Unique identifier for the research topic.
     * Used to track the request across microservice boundaries (Kafka)
     * and correlate final AI results with the original user input.
     */
    private UUID topicId;

    /**
     * The raw search query provided by the user.
     * Example: "Budget 2026 for middle class".
     * This query is used by processing agents to fetch relevant video metadata.
     */
    private String query;
}

package com.vishal.aiyoutube.youtube_processing_service.service;

import com.vishal.aiyoutube.youtube_processing_service.dto.TopicSubmittedEvent;

/**
 * Interface for the YouTube Orchestrator.
 * Defines the contract for coordinating the end-to-end data gathering pipeline.
 */
public interface IYouTubeOrchestrator {

    /**
     * Processes a research topic by searching YouTube, extracting transcripts,
     * and streaming data to Kafka for AI synthesis.
     * @param event The original topic submission event.
     */
    void processTopic(TopicSubmittedEvent event);
}
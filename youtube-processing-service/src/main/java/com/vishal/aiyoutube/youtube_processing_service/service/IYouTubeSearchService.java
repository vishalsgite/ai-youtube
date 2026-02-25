package com.vishal.aiyoutube.youtube_processing_service.service;

import com.vishal.aiyoutube.youtube_processing_service.dto.YoutubeSearchResponse;
import reactor.core.publisher.Mono;

/**
 * Interface for discovering video content via the YouTube Data API.
 * Uses Project Reactor (Mono) for non-blocking, asynchronous execution.
 */
public interface IYouTubeSearchService {

    /**
     * Searches for videos based on a specific query.
     * @param query The refined research topic (e.g., "Budget 2026 Middle Class").
     * @param maxResults The number of video sources to fetch.
     * @return A Mono containing the structured API response metadata.
     */
    Mono<YoutubeSearchResponse> searchVideos(String query, int maxResults);
}
package com.vishal.aiyoutube.youtube_processing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

/**
 * High-performance event DTO used to stream processed video data to AI agents.
 * This class encapsulates a single video's intelligence, allowing the
 * Distributed System to begin synthesis without waiting for the entire batch.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoDataProcessedEvent {

    /**
     * Unique identifier for the research topic.
     * Maps this video back to the original query (e.g., Budget 2026 for Middle Class)
     * so Llama-3 and Grok can maintain contextual consistency.
     */
    private UUID topicId;

    /**
     * The core payload: Contains the specific metadata and transcript
     * segments for a single video.
     */
    private VideoTranscriptData videoData;

    /**
     * Progress tracking: The current index of this video in the search results.
     * Allows the AI service to update the UI progress bar in real-time.
     */
    private int currentCount;

    /**
     * The total number of videos the system intended to process.
     * Critical for the AI service to know when to finalize the Consensus Score.
     */
    private int totalVideos;

    /**
     * Inner static class representing the detailed content of the video.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoTranscriptData {

        /**
         * YouTube's unique identifier for the video.
         */
        private String videoId;

        /**
         * The full title of the YouTube video.
         */
        private String title;

        /**
         * The direct hyperlink to the video.
         */
        private String videoUrl;

        /**
         * The collection of timestamped text snippets.
         * These are used by the AI to build the "Source Intelligence" cards
         * and the consensus summary.
         */
        private List<TranscriptSegmentDTO> segments;
    }
}
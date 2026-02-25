package com.vishal.aiyoutube.youtube_processing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Data Transfer Object representing the full transcript and metadata of a video.
 * This is the primary data structure used by AI agents to perform
 * multi-video consensus and sentiment analysis.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoTranscriptData {

    /**
     * The unique identifier assigned by YouTube to the video.
     * Used for deduplication and internal tracking within the microservices.
     */
    private String videoId;

    /**
     * The original title of the YouTube video.
     * Provided to the AI models to help establish context for the
     * synthesized summary.
     */
    private String title;

    /**
     * The direct URL to the video.
     * Enables the "Source Intelligence" feature on the UI, allowing
     * users to verify AI insights at the source.
     */
    private String videoUrl;

    /**
     * A collection of timestamped text snippets.
     * This list forms the bulk of the "Raw Intelligence" passed through
     * Kafka to be analyzed for consensus on complex topics like
     * 'Budget 2026 for the Middle Class'.
     */
    private List<TranscriptSegmentDTO> segments;
}

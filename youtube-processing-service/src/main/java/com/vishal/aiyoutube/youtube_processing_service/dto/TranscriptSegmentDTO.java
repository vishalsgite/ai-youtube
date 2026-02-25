package com.vishal.aiyoutube.youtube_processing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a single segment of a video transcript.
 * These segments are aggregated to provide the full context required by
 * AI models for synthesis.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptSegmentDTO {

    /**
     * The timestamp (in seconds) where this specific segment begins.
     * Crucial for the "Source Intelligence" feature on the UI, allowing
     * users to jump to the exact moment in the YouTube video.
     */
    private Double start;

    /**
     * The raw text content captured at this timestamp.
     * This text is processed by Llama-3 and Grok agents to identify
     * key insights and consensus.
     */
    private String text;
}
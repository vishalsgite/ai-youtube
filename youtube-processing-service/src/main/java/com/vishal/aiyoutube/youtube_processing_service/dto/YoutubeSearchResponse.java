package com.vishal.aiyoutube.youtube_processing_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object for parsing YouTube Data API v3 search results.
 * This class is specifically structured to map the 'Search: list' JSON response
 * into a type-safe Java object for the processing pipeline.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeSearchResponse {

    /**
     * The list of search results returned by the API.
     * Each item represents a potential source for the multi-agent synthesis.
     */
    private List<Item> items;

    /**
     * Represents an individual search result.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private Id id;
        private Snippet snippet;
    }

    /**
     * Contains the unique identifier for the video.
     * Essential for generating the videoUrl and fetching transcript data later
     * in the distributed pipeline.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Id {
        private String videoId;
    }

    /**
     * Contains high-level metadata about the video.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        /**
         * The title of the video.
         * Used by Llama-3 and Grok models to establish research context
         * for queries like 'Budget 2026'.
         */
        private String title;
    }
}
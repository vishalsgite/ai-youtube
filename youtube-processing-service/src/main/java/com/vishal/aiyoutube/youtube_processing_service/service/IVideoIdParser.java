package com.vishal.aiyoutube.youtube_processing_service.service;

/**
 * Interface for normalizing YouTube identifiers.
 * This contract ensures that all components in the microservice
 * can resolve a raw input into a valid 11-character Video ID.
 */
public interface IVideoIdParser {

    /**
     * Normalizes the input string to extract the YouTube Video ID.
     * @param input Full URL, embedded link, or raw ID string.
     * @return The 11-character YouTube Video ID.
     */
    String parseId(String input);
}

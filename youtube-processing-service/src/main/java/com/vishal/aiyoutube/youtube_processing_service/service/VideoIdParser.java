package com.vishal.aiyoutube.youtube_processing_service.service;

import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility component responsible for normalizing YouTube identifiers.
 * It ensures the pipeline receives a consistent 11-character Video ID
 * regardless of the input URL format.
 */
@Component
public class VideoIdParser implements IVideoIdParser {

    /**
     * Extracts the 11-character YouTube Video ID from various URL formats.
     * * * SUPPORTED FORMATS:
     * - Standard: youtube.com/watch?v=VIDEO_ID
     * - Shortened: youtu.be/VIDEO_ID
     * - Embedded: youtube.com/embed/VIDEO_ID
     * - URL Encoded: watch?v%3DVIDEO_ID
     * * @param input The raw string or URL provided by the search agent.
     * @return The extracted 11-character ID, or the raw input as a fallback.
     */
    @Override
    public String parseId(String input) {
        // Guard clause for null or empty strings to prevent processing errors
        if (input == null || input.isBlank()) return null;

        /**
         * Fast-Path Optimization:
         * If the input is already a raw 11-character ID, return it immediately
         * to skip expensive regex processing.
         */
        if (input.length() == 11 && !input.contains("/") && !input.contains("?")) {
            return input;
        }

        /**
         * Regex Architecture:
         * This pattern uses 'positive lookbehind' to identify the Video ID
         * following various common YouTube URL delimiters used in
         * desktop, mobile, and embedded players.
         */
        String regex = "(?<=watch\\?v=|/videos/|/embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group();
        }

        /**
         * Fallback Logic:
         * If no pattern matches, we return the raw input. This is useful
         * for resilient processing of IDs that might be partially sanitized.
         */
        return input;
    }
}
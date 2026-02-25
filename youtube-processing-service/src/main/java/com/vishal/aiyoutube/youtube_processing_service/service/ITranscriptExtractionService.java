package com.vishal.aiyoutube.youtube_processing_service.service;

import com.vishal.aiyoutube.youtube_processing_service.dto.TranscriptSegmentDTO;
import java.util.List;

/**
 * Interface defining the contract for extracting transcript data.
 * Essential for decoupled system design and unit testing.
 */
public interface ITranscriptExtractionService {

    /**
     * Retrieves timestamped transcript segments for a given video.
     * @param videoId The unique YouTube video identifier.
     * @return A list of segments containing start times and raw text.
     */
    List<TranscriptSegmentDTO> fetchTranscript(String videoId);
}
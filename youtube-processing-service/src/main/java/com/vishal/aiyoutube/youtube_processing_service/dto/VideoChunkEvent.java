package com.vishal.aiyoutube.youtube_processing_service.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoChunkEvent {
    private UUID topicId;
    private VideoTranscriptData videoData;
    private int currentCount;
    private int totalVideos;
}
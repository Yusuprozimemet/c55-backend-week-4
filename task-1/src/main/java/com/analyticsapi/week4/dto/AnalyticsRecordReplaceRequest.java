package com.analyticsapi.week4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class AnalyticsRecordReplaceRequest {

    @NotNull
    private Instant timestamp;

    @NotBlank
    @Size(min =1, max = 64)
    private String eventType;

    @NotBlank
    @Size(min =1, max = 64)
    private String eventSource;

    @NotBlank
    @Size(min =1, max = 64)
    private String sessionId;

}
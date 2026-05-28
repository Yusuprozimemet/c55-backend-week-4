package com.analyticsapi.week4.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRecord {
    private String id;
    private Instant timestamp;
    private String eventType;
    private String eventSource;
    private String sessionId;
}

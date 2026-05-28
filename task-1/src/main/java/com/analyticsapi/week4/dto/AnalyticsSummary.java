package com.analyticsapi.week4.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AnalyticsSummary {

    private long totalRecords;
    private Map<String, Long> totalsByEventType;
    private long uniqueSessions;
}
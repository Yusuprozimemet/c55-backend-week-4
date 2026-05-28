package com.analyticsapi.week4.controller;

import com.analyticsapi.week4.dto.AnalyticsSummary;
import com.analyticsapi.week4.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/analytics-summary")
public class AnalyticsSummaryController {

    private final AnalyticsService service;

    public AnalyticsSummaryController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<AnalyticsSummary> getSummary(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String eventSource,
            @RequestParam(required = false) String sessionId
    ) {
        AnalyticsSummary summary = service.getSummary(from, to, eventType, eventSource, sessionId);
        return ResponseEntity.ok(summary);
    }
}

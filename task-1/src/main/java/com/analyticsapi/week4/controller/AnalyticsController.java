package com.analyticsapi.week4.controller;

import com.analyticsapi.week4.dto.AnalyticsRecordCreateRequest;
import com.analyticsapi.week4.model.AnalyticsRecord;
import com.analyticsapi.week4.service.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("/api/analytics-records")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AnalyticsRecord> create(
            @RequestBody @Valid AnalyticsRecordCreateRequest record) {
        AnalyticsRecord created = service.create(record);
        URI location = URI.create("/api/analytics-records/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyticsRecord> getById(@PathVariable String id) {
        AnalyticsRecord record = service.getById(id);
        return ResponseEntity.ok(record);
    }
}
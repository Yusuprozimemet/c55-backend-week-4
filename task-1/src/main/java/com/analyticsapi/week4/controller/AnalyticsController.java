package com.analyticsapi.week4.controller;

import com.analyticsapi.week4.dto.AnalyticsRecordCreateRequest;
import com.analyticsapi.week4.model.AnalyticsRecord;
import com.analyticsapi.week4.service.AnalyticsService;
import com.analyticsapi.week4.dto.AnalyticsRecordReplaceRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;


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

    @GetMapping
    public ResponseEntity<List<AnalyticsRecord>> list(
        @RequestParam(required = false) Instant from,
        @RequestParam(required = false) Instant to,
        @RequestParam(required = false) String eventType,
        @RequestParam(required = false) String eventSource,
        @RequestParam(required = false) String sessionId,
        @RequestParam(defaultValue = "100") int limit,
        @RequestParam(defaultValue = "0") int offset
    ){
        List<AnalyticsRecord> records = service.list(from, to, eventType, eventSource, sessionId, limit, offset);
        return ResponseEntity.ok(records);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalyticsRecord> replace (
        @PathVariable String id,
        @Valid @RequestBody AnalyticsRecordReplaceRequest request){
            AnalyticsRecord record = service.replace(id, request);
            return ResponseEntity.ok(record);
        }
}
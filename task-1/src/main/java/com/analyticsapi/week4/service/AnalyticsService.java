package com.analyticsapi.week4.service;

import java.util.UUID;

import com.analyticsapi.week4.dto.AnalyticsRecordCreateRequest;
import com.analyticsapi.week4.exception.RecordNotFoundException;
import com.analyticsapi.week4.model.AnalyticsRecord;
import com.analyticsapi.week4.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final AnalyticsRepository repository;

    public AnalyticsService(AnalyticsRepository repository){
        this.repository = repository;
    }

    public AnalyticsRecord create (AnalyticsRecordCreateRequest request){
        AnalyticsRecord record = AnalyticsRecord.builder()
                .id(UUID.randomUUID().toString())
                .timestamp(request.getTimestamp())
                .eventType(request.getEventType())
                .eventSource(request.getEventSource())
                .sessionId(request.getSessionId())
                .build();
        return repository.save(record);
    }

    public AnalyticsRecord getById(String id){
        return repository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException(id));
    }
}
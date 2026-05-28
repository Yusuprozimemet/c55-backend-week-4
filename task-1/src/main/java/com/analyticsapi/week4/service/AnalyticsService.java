package com.analyticsapi.week4.service;

import java.util.UUID;

import com.analyticsapi.week4.dto.AnalyticsRecordCreateRequest;
import com.analyticsapi.week4.dto.AnalyticsRecordReplaceRequest;
import com.analyticsapi.week4.exception.RecordNotFoundException;
import com.analyticsapi.week4.model.AnalyticsRecord;
import com.analyticsapi.week4.repository.AnalyticsRepository;
import com.analyticsapi.week4.dto.AnalyticsRecordReplaceRequest;
import org.springframework.stereotype.Service;
import com.analyticsapi.week4.dto.AnalyticsSummary;
import java.util.Map;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;



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

    public List<AnalyticsRecord> list(
        Instant from,
        Instant to,
        String eventType,
        String eventSource,
        String sessionId,
        int limit,
        int offset){

        return repository.findAll().stream()
            .filter(r -> from == null || !r.getTimestamp().isBefore(from))
            .filter(r -> to == null || !r.getTimestamp().isAfter(to))
            .filter(r -> eventType == null || r.getEventType().equals(eventType))
            .filter(r -> eventSource == null || r.getEventSource().equals(eventSource))
            .filter(r -> sessionId == null || r.getSessionId().equals(sessionId))
            .sorted(Comparator.comparing(AnalyticsRecord::getTimestamp).reversed())
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
        }

    public void delete(String id){
        boolean removed = repository.deleteById(id);
            if(!removed){
                throw new RecordNotFoundException(id);
            }
        }

    public AnalyticsRecord replace(String id, AnalyticsRecordReplaceRequest request){
        repository.findById(id)
                 .orElseThrow(() -> new RecordNotFoundException(id));

        AnalyticsRecord updated = AnalyticsRecord.builder()
                .id(id)
                .timestamp(request.getTimestamp())
                .eventType(request.getEventType())
                .eventSource(request.getEventSource())
                .sessionId(request.getSessionId())
                .build();

        return repository.save(updated);
    }

    public AnalyticsSummary getSummary(
            Instant from,
            Instant to,
            String eventType,
            String eventSource,
            String sessionId){

        List<AnalyticsRecord> filtered = list(from, to, eventType, eventSource, sessionId, Integer.MAX_VALUE, 0);

        Map<String, Long> totalsByEventType = filtered.stream()
                .collect(Collectors.groupingBy(
                        AnalyticsRecord::getEventType,
                        Collectors.counting()
                ));

        long uniqueSessions = filtered.stream()
                .map(AnalyticsRecord::getSessionId)
                .distinct()
                .count();

        return AnalyticsSummary.builder()
                .totalRecords(filtered.size())
                .totalsByEventType(totalsByEventType)
                .uniqueSessions(uniqueSessions)
                .build();
    }


}
    
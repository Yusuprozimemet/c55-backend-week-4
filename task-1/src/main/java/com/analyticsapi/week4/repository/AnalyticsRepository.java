package com.analyticsapi.week4.repository;

import com.analyticsapi.week4.model.AnalyticsRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AnalyticsRepository {

    private final List<AnalyticsRecord> store = new ArrayList<>();

    public AnalyticsRecord save(AnalyticsRecord record) {
        store.removeIf(r -> r.getId().equals(record.getId()));
        store.add(record);
        return record;
    }

    public List<AnalyticsRecord> findAll(){
        return new ArrayList<>(store);
    }

    public Optional<AnalyticsRecord> findById(String id) {
        return store.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public boolean deleteById(String id){
        return store.removeIf(r -> r.getId().equals(id));
    }

}
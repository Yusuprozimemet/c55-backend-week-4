package com.analyticsapi.week4.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldError> details;

    @Data
    @Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}

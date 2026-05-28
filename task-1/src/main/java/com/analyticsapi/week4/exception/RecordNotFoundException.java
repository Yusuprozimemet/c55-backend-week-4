package com.analyticsapi.week4.exception;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(String id){
        super("Record not found: " + id);
    }
}
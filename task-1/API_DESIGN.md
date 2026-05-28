# API Design - Analytics API

## Base URL

`/api`

## Endpoint overview

```mermaid
graph TD
    Client([Client])

    subgraph Records ["Resource: /api/analytics-records"]
        POST["POST /api/analytics-records
        Body: timestamp, eventType, eventSource, sessionId
        201 Created, 400 Bad Request"]

        GET_LIST["GET /api/analytics-records
        Filters: from, to, eventType, eventSource, sessionId
        Pagination: limit, offset
        200 OK"]

        subgraph ByID ["By ID: /api/analytics-records/{id}"]
            GET_ONE["GET /{id}
            200 OK, 404 Not Found"]

            PUT["PUT /{id}
            Body: timestamp, eventType, eventSource, sessionId
            200 OK, 404 Not Found, 400 Bad Request"]

            DEL["DELETE /{id}
            204 No Content, 404 Not Found"]
        end
    end

    subgraph Summary ["Resource: /api/analytics-summary"]
        GET_SUM["GET /api/analytics-summary
        Filters: from, to, eventType, eventSource, sessionId
        200 OK: totalRecords, totalsByEventType, uniqueSessions"]
    end

    Client -->|create| POST
    Client -->|list + filter| GET_LIST
    Client -->|fetch one| GET_ONE
    Client -->|replace one| PUT
    Client -->|delete one| DEL
    Client -->|get summary| GET_SUM
```

## Resource: analytics records

### Data model

AnalyticsRecord

```
{
  "id": "uuid",
  "timestamp": "2026-05-28T12:34:56Z",
  "eventType": "page_view",
  "eventSource": "web",
  "sessionId": "anon-123"
}
```

Notes:
- `id` is server-generated and acts as the trace ID.
- `timestamp` uses ISO-8601 in UTC (e.g., `2026-05-28T12:34:56Z`).
- `sessionId` must be anonymized. No personal data is allowed.

```mermaid
classDiagram
    class AnalyticsRecord {
        +String id
        +Instant timestamp
        +String eventType
        +String eventSource
        +String sessionId
    }

    class AnalyticsRecordCreateRequest {
        +Instant timestamp
        +String eventType
        +String eventSource
        +String sessionId
    }

    class AnalyticsRecordReplaceRequest {
        +Instant timestamp
        +String eventType
        +String eventSource
        +String sessionId
    }

    class AnalyticsSummary {
        +long totalRecords
        +Map~String, Long~ totalsByEventType
        +long uniqueSessions
    }

    class ErrorResponse {
        +Instant timestamp
        +int status
        +String error
        +String message
        +String path
        +List~FieldError~ details
    }

    AnalyticsRecordCreateRequest ..> AnalyticsRecord : server adds id
    AnalyticsRecordReplaceRequest ..> AnalyticsRecord : server keeps id
```

## Endpoints

### Create record

`POST /api/analytics-records`

Request body (AnalyticsRecordCreateRequest)

```
{
  "timestamp": "2026-05-28T12:34:56Z",
  "eventType": "page_view",
  "eventSource": "web",
  "sessionId": "anon-123"
}
```

Validation rules
- `timestamp`: required, valid ISO-8601.
- `eventType`: required, non-blank, length 1-64.
- `eventSource`: required, non-blank, length 1-64.
- `sessionId`: required, non-blank, length 1-64.

Response
- `201 Created` with the created record.
- `Location` header points to `/api/analytics-records/{id}`.

```mermaid
sequenceDiagram
    participant Client
    participant API as API Layer (Controller)
    participant Service as Business Logic (Service)
    participant DB as Database Layer (Repository)

    Client->>API: POST /api/analytics-records<br/>{ timestamp, eventType, eventSource, sessionId }
    API->>API: deserialize JSON into AnalyticsRecordCreateRequest DTO
    API->>API: validate fields (not blank, length 1-64, ISO-8601)
    API-->>Client: 400 Bad Request + error details (if validation fails)
    API->>Service: create(request)
    Service->>Service: generate UUID as trace ID
    Service->>Service: build AnalyticsRecord model
    Service->>DB: save(record)
    DB-->>Service: saved AnalyticsRecord
    Service-->>API: AnalyticsRecord
    API->>API: set Location header to /api/analytics-records/{id}
    API-->>Client: 201 Created + { id, timestamp, eventType, eventSource, sessionId }
```

### List records (with filters)

`GET /api/analytics-records`

Query parameters (all optional)
- `from`: ISO-8601 start time, inclusive.
- `to`: ISO-8601 end time, inclusive.
- `eventType`: filter by event type.
- `eventSource`: filter by event source.
- `sessionId`: filter by anonymized session ID.
- `limit`: default 100.
- `offset`: default 0.

Behavior
- Filters are combined with AND.
- If only `from` is provided, return records on or after `from`.
- If only `to` is provided, return records on or before `to`.
- If both are provided and `from` is after `to`, an empty list is returned.
- Results are sorted by `timestamp` descending.

Response
- `200 OK` with a JSON array of records.

### Get record by ID

`GET /api/analytics-records/{id}`

Response
- `200 OK` with the record.
- `404 Not Found` if the ID does not exist.

### Replace record by ID

`PUT /api/analytics-records/{id}`

Request body (AnalyticsRecordReplaceRequest)

```
{
  "timestamp": "2026-05-28T12:34:56Z",
  "eventType": "page_view",
  "eventSource": "web",
  "sessionId": "anon-123"
}
```

Validation rules
- Same as create.
- Full replace: all fields are required.

Response
- `200 OK` with the updated record.
- `404 Not Found` if the ID does not exist.

### Delete record by ID

`DELETE /api/analytics-records/{id}`

Response
- `204 No Content` if deleted.
- `404 Not Found` if the ID does not exist.

### Summary

`GET /api/analytics-summary`

Query parameters (optional, same as list)
- `from`, `to`, `eventType`, `eventSource`, `sessionId`

Response body

```
{
  "totalRecords": 123,
  "totalsByEventType": {
    "page_view": 100,
    "signup": 23
  },
  "uniqueSessions": 17
}
```

Response
- `200 OK` with the summary of records that match the filters.

## Error responses

All error responses use the following shape:

```
{
  "timestamp": "2026-05-28T12:34:56Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/analytics-records",
  "details": [
    { "field": "eventType", "message": "must not be blank" }
  ]
}
```

Common errors
- `400 Bad Request` for validation failures (missing or blank required fields, invalid JSON).
- `404 Not Found` when a record ID does not exist.

## Architecture

```mermaid
graph LR
    Client([Client])

    subgraph "API Layer"
        C1["AnalyticsController"]
        C2["AnalyticsSummaryController"]
        C3["GlobalExceptionHandler"]
    end

    subgraph "Business Logic Layer"
        S1["AnalyticsService"]
    end

    subgraph "DTOs"
        D1["AnalyticsRecordCreateRequest"]
        D2["AnalyticsRecordReplaceRequest"]
        D3["AnalyticsSummary"]
        D4["ErrorResponse"]
    end

    subgraph "Model"
        M1["AnalyticsRecord"]
    end

    subgraph "Database Layer"
        R1["AnalyticsRepository<br/>(in-memory List)"]
    end

    Client -->|HTTP| C1
    Client -->|HTTP| C2
    C1 --> S1
    C2 --> S1
    S1 --> R1
    C1 -.->|uses| D1
    C1 -.->|uses| D2
    C1 -.->|uses| D4
    C2 -.->|uses| D3
    C3 -.->|produces| D4
    S1 -.->|uses| M1
    R1 -.->|stores| M1
```

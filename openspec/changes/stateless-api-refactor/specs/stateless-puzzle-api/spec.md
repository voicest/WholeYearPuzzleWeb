## ADDED Requirements

### Requirement: Board endpoint accepts date parameter
The system SHALL accept an optional `date` query parameter (format `YYYY-MM-DD`) on `GET /api/board` and return board cells with the two corresponding date cells (month + day) marked as TARGET state.

#### Scenario: Board with specific date
- **WHEN** a client sends `GET /api/board?date=2026-03-11`
- **THEN** the response SHALL contain all board cells with the "Mar" cell and "11" cell in TARGET state and all other fillable cells in FILLABLE state

#### Scenario: Board defaults to today when date omitted
- **WHEN** a client sends `GET /api/board` without a date parameter
- **THEN** the response SHALL use the server's current date to determine TARGET cells

#### Scenario: Invalid date returns error
- **WHEN** a client sends `GET /api/board?date=invalid`
- **THEN** the system SHALL return HTTP 400 Bad Request

### Requirement: Solve endpoint accepts date parameter
The system SHALL accept an optional `date` query parameter (format `YYYY-MM-DD`) on `POST /api/solve` and solve the puzzle for that date, excluding the two target cells from the fillable set.

#### Scenario: Solve for specific date
- **WHEN** a client sends `POST /api/solve?date=2026-12-25`
- **THEN** the response SHALL contain a valid solution for Dec 25 (all fillable cells except "Dec" and "25" are covered exactly once by the 9 pieces)

#### Scenario: Solve defaults to today when date omitted
- **WHEN** a client sends `POST /api/solve` without a date parameter
- **THEN** the system SHALL solve for the server's current date

### Requirement: No shared mutable state between requests
The system SHALL NOT store any per-request state (target date, target cells) in shared controller or service fields. Each request SHALL compute its result independently.

#### Scenario: Concurrent requests with different dates
- **WHEN** two concurrent requests are made — `GET /api/board?date=2026-01-01` and `GET /api/board?date=2026-12-31`
- **THEN** each response SHALL have the correct TARGET cells for its respective date, with no cross-contamination

### Requirement: updateTargetDate endpoint is removed
The system SHALL NOT expose the `POST /api/updateTargetDate` endpoint.

#### Scenario: Calling removed endpoint returns 404
- **WHEN** a client sends `POST /api/updateTargetDate?date=2026-03-11`
- **THEN** the system SHALL return HTTP 404 Not Found

### Requirement: Frontend passes date parameter on API calls
The frontend SHALL pass the selected date as a query parameter on `/api/board` and `/api/solve` requests instead of calling a separate `updateTargetDate` endpoint.

#### Scenario: Date change triggers board refetch with parameter
- **WHEN** the user selects a new date in the date picker
- **THEN** the frontend SHALL fetch `GET /api/board?date=YYYY-MM-DD` with the selected date
- **AND** SHALL NOT call `POST /api/updateTargetDate`

#### Scenario: Solve uses selected date
- **WHEN** the user clicks the Solve button
- **THEN** the frontend SHALL send `POST /api/solve?date=YYYY-MM-DD` with the currently selected date

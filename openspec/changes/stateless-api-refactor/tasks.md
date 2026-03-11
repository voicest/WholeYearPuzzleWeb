## 1. Board — Immutable Target Computation

- [x] 1.1 Add `Board.getBoardCellsForDate(String monthLabel, String dayLabel)` method that returns `List<BoardCell>` with TARGET state computed without mutating the grid
- [x] 1.2 Add date-parsing helper method to `SolverController` that converts `YYYY-MM-DD` string to month label and day label, defaulting to today when null
- [x] 1.3 Add validation for the date parameter — return 400 Bad Request for invalid or unparseable dates

## 2. Controller — Stateless Endpoints

- [x] 2.1 Modify `GET /api/board` to accept optional `date` query parameter and use `Board.getBoardCellsForDate()` instead of reading mutable grid state
- [x] 2.2 Modify `POST /api/solve` to accept optional `date` query parameter, compute target cells from date, and pass filtered fillable cells to the Solver
- [x] 2.3 Remove `POST /api/updateTargetDate` endpoint
- [x] 2.4 Remove mutable fields from `SolverController` (`targetCells` list, mutable board state mutation in constructor) — keep only immutable `board` and `pieces` singletons

## 3. Frontend — Pass Date Per-Request

- [x] 3.1 Update `App.js` board fetch (on mount and after date change) to pass `?date=` query parameter to `/api/board`
- [x] 3.2 Update `App.js` solve fetch to pass `?date=` query parameter to `/api/solve`
- [x] 3.3 Remove the `POST /api/updateTargetDate` fetch call from `handleDateChange`

## 4. Tests

- [x] 4.1 Update `SolverControllerTest` to pass `date` query parameter on `/api/board` and `/api/solve` requests
- [x] 4.2 Add test: `GET /api/board?date=2026-03-11` returns correct TARGET cells for Mar and 11
- [x] 4.3 Add test: `GET /api/board` without date parameter defaults to today
- [x] 4.4 Add test: `GET /api/board?date=invalid` returns 400
- [x] 4.5 Add test: `POST /api/updateTargetDate` returns 404 (endpoint removed)
- [x] 4.6 Build backend and frontend to confirm no compilation errors

## 5. Cleanup

- [x] 5.1 Remove `board.reset()` and `board.setTarget()` calls from `SolverController` (constructor and updateTargetDate are gone)
- [x] 5.2 Verify all existing tests pass with the new stateless API

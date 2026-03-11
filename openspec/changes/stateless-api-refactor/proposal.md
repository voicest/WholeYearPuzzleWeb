## Why

The backend `SolverController` is a Spring singleton that holds mutable board state (target cells, board grid). When two players change the target date concurrently, one overwrites the other — `/api/solve` can return a solution for the wrong date. The application cannot support concurrent users.

## What Changes

- **BREAKING**: Remove `POST /api/updateTargetDate` endpoint — target date will no longer be stored server-side
- Modify `GET /api/board` to accept a `date` query parameter and compute target cells per-request without mutating any shared state
- Modify `POST /api/solve` to accept a `date` query parameter and solve for the given date per-request
- Make the `Board` and `Piece` definitions immutable singletons — no mutation after startup
- Add a method to `Board` that computes board cells with target state for a given date without modifying the board grid
- Update frontend to pass `date` param on `/api/board` and `/api/solve` calls, and remove the `updateTargetDate` fetch

## Capabilities

### New Capabilities
- `stateless-puzzle-api`: Backend API accepts target date as a per-request parameter, enabling concurrent multi-player use with no shared mutable state

### Modified Capabilities

## Impact

- **Backend**: `SolverController` — remove mutable fields, add date parameter to endpoints; `Board` — add immutable board-cell computation method; `Solver` — accept target exclusion cells as parameter
- **Frontend**: `App.js` — pass `?date=` to `/api/board` and `/api/solve`, remove `POST /api/updateTargetDate` call
- **API contract**: Breaking change for any existing consumers of `POST /api/updateTargetDate` (removed) and `GET /api/board` / `POST /api/solve` (new required `date` param)
- **Tests**: `SolverControllerTest` — update to pass date parameter; add concurrency test for parallel requests with different dates

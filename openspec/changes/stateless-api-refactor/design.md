## Context

The `SolverController` is a Spring singleton that holds a single mutable `Board` instance and a `targetCells` list. When a user calls `POST /api/updateTargetDate`, the board is mutated in-place (`board.reset()`, then `board.setTarget()`). All subsequent `/api/board` and `/api/solve` calls read from this shared mutable state. This means concurrent users overwrite each other's target dates.

The board shape, labels, and piece definitions are inherently static — they never change. The only per-request variable is the target date (which two cells to leave uncovered).

The frontend already holds `targetDate` in React state and currently sends it to the backend via a separate `POST /api/updateTargetDate` call before fetching the board or solving.

## Goals / Non-Goals

**Goals:**
- Eliminate all shared mutable state from the backend so concurrent requests with different dates cannot interfere
- Pass the target date as a query parameter on `/api/board` and `/api/solve` so each request is self-contained
- Keep the `Board` and `List<Piece>` as immutable singletons shared across all requests
- Remove the `POST /api/updateTargetDate` endpoint entirely
- Update the frontend to pass `date` on each API call instead of calling `updateTargetDate`

**Non-Goals:**
- Server-side sessions or per-user state
- Leaderboards, timers, or user identity (future work)
- Caching solver results (optimisation for later)
- Changing the puzzle logic, piece definitions, or board shape

## Decisions

### 1. Date as query parameter, not path segment
**Decision**: Use `?date=YYYY-MM-DD` as a query parameter on `GET /api/board` and `POST /api/solve`.

**Rationale**: Query parameters are idiomatic for filtering/configuring a response without changing the resource identity. Path segments (`/api/board/2026-03-11`) would imply distinct resources, which is misleading — the board structure is the same for every date.

**Alternative considered**: Keep `POST /api/updateTargetDate` but scope it to a session. Rejected — adds session complexity for no benefit when the date can be passed per-request.

### 2. Compute target state per-request without mutating Board
**Decision**: Add a `Board.getBoardCellsForDate(String monthLabel, String dayLabel)` method that returns `List<BoardCell>` with TARGET state computed on the fly, without modifying the internal grid.

**Rationale**: The board shape and labels are immutable. Only the TARGET/FILLABLE distinction changes by date. Computing this per-request avoids all mutation and makes the Board object thread-safe.

**Alternative considered**: Deep-copy the Board per request and mutate the copy. Rejected — unnecessary allocation when we can compute the output directly.

### 3. Solver accepts target cells as exclusion parameter
**Decision**: Modify the solve flow so the controller computes target cells from the date parameter, then passes them to the `Solver` as cells to exclude from the fillable set.

**Rationale**: The `Solver` currently reads `board.getAllFillableCells()` which depends on the board's mutable state. Instead, the controller will compute the exclusion set and pass a filtered fillable cells list or the target cells to exclude.

### 4. Default to today's date when parameter is omitted
**Decision**: If the `date` query parameter is missing, default to today's date (server's local date).

**Rationale**: Preserves backward compatibility for direct browser access and matches the frontend's default behaviour of selecting today's date on load.

## Risks / Trade-offs

- **[API breaking change]** → `POST /api/updateTargetDate` is removed. Mitigated by the fact there are no known external consumers; the only client is the React frontend which will be updated simultaneously.
- **[Date parsing on every request]** → Negligible cost. Parsing a date string is sub-microsecond.
- **[Invalid date handling]** → Need to validate the date parameter (valid month 1-12, valid day 1-31). Return 400 Bad Request for invalid dates.

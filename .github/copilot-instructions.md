# Whole Year Puzzle Solver — Project Context

## Overview

This is a **"Whole Year Puzzle" solver** — a web application that solves a physical date-based tiling puzzle. The puzzle consists of an irregularly-shaped board with cells labelled with months (Jan–Dec) and days (1–31). The player selects a target date (month + day), which marks two cells as **targets** (to be left uncovered), and must then tile all remaining cells exactly using 9 uniquely-shaped polyomino pieces. The application provides both an **automated solver** (using an Exact Cover / Dancing Links algorithm) and a **manual drag-and-drop interface** for placing pieces by hand.

## Architecture

The project is a **full-stack monorepo** with two main parts:

```
WholeYearPuzzleWeb/
├── src/
│   ├── main/java/com/wholeyear/   ← Spring Boot backend (REST API + solver engine)
│   └── frontend/                   ← React frontend (Create React App)
├── build.gradle                    ← Gradle build (backend + frontend integration)
└── gradle/                         ← Gradle wrapper config
```

### Deployment Model

The React frontend is built as part of the Gradle build and served as static resources by Spring Boot at runtime. A single `./gradlew build` produces a fat JAR that contains both the API and the UI. The `com.github.node-gradle.node` Gradle plugin handles downloading Node.js, running `npm install`, and `npm run build`. The output from `src/frontend/build/` is copied into `build/resources/main/static/` so Spring Boot auto-serves it.

- **`WebConfig.java`** — Registers an error page that redirects 404s to `/index.html`, enabling React SPA routing.
- Spring Boot's `WelcomePageHandlerMapping` automatically serves `static/index.html` as the root (`/`) page.

### Backend — Java / Spring Boot

- **Framework**: Spring Boot 3.2.5, Java 17
- **Build tool**: Gradle (wrapper included)
- **Entry point**: `com.wholeyear.solver.WholeYearSolverApiApplication` (standard `@SpringBootApplication`)
- **Base URL prefix**: `/api`

#### Packages

| Package | Purpose |
|---|---|
| `com.wholeyear.model` | Domain model classes (Board, Cell, Piece, DTOs) |
| `com.wholeyear.solver` | Spring Boot application class and REST controller |
| `com.wholeyear.util` | Puzzle definition, solver logic, Exact Cover (DLX) implementation |

#### Key Classes

**Model layer** (`com.wholeyear.model`):

- **`Board`** — Represents the irregular puzzle board as a 2D grid. Each cell has a `CellState` (`OFF_BOARD`, `FILLABLE`, `TARGET`, `BLOCKED`) and a label (month name or day number). Constructed from an ASCII shape template and a parallel label grid. Supports operations: `block()`, `setTarget()`, `reset()`, `findCellByLabel()`, `getAllFillableCells()`.
- **`Cell`** — Immutable (row, col) coordinate pair. Used throughout for board positions and piece shapes.
- **`Piece`** — A puzzle piece defined by an ASCII template of `#` characters. Computes all distinct orientations (up to 8: 4 rotations × 2 flips) using rotation/flip transforms with deduplication via coordinate signatures.
- **`BoardCell`** — Serializable DTO for sending board state to the frontend (row, col, label, state).
- **`PieceDto`** — Serializable DTO for sending piece definitions to the frontend (id, name, canonical shape).

**Controller** (`com.wholeyear.solver`):

- **`SolverController`** — REST controller with these endpoints:
  - `GET /api/board` → Returns all board cells as `List<BoardCell>` (full grid including OFF_BOARD cells)
  - `GET /api/pieces` → Returns all 9 puzzle pieces as `List<PieceDto>` (with integer index IDs)
  - `POST /api/solve` → Runs the solver and returns the solution as `List<PlacementDto>` (piece index + covered cells)
  - `POST /api/updateTargetDate?date=YYYY-MM-DD` → Updates which two cells (month + day) are marked as targets, resets the board

  On startup, the controller initialises the board and sets target cells to today's date.

**Solver engine** (`com.wholeyear.util`):

- **`Definition`** — Static factory methods:
  - `loadAllPieces()` — Defines all 9 puzzle pieces as ASCII templates
  - `createWholeYearPuzzleBoard()` — Defines the 7×9 irregular board shape and its month/day labels
- **`Solver`** — Orchestrates solving:
  1. Gets all fillable cells from the board
  2. Generates all valid placements (every piece × every orientation × every valid board position)
  3. Builds an Exact Cover matrix where columns = fillable cells + one column per piece (ensuring each piece is used exactly once)
  4. Delegates to `ExactCoverSolver`
- **`ExactCoverSolver`** — Full Dancing Links (DLX) implementation of Knuth's Algorithm X. Uses a toroidal doubly-linked list structure with column headers, cover/uncover operations, and minimum-size column heuristic.
- **`Placement`** — Immutable data class representing one possible way to place a piece: piece ID, orientation index, anchor position, and list of absolute board cells covered.
- **`PlacementDto`** — Simplified DTO for API responses: piece index + covered cells.

### Frontend — React

- **Framework**: React 19.1 (Create React App / react-scripts 5.0.1)
- **Location**: `src/frontend/`
- **Dev server**: Runs on port 3000 (proxied to backend, or backend serves static build)
- **No routing library** — single-page app with one view

#### Components

- **`App.js`** — Root component. Manages all state:
  - Fetches board data and piece definitions from `/api/board` and `/api/pieces` on mount
  - Handles date selection → calls `POST /api/updateTargetDate` then refetches board
  - Handles "Solve" button → calls `POST /api/solve`
  - Manages drag-and-drop piece placement (manual mode):
    - Validates drops against board bounds, existing placements, and target cells
    - Tracks which pieces are "used"
  - Piece rotation (spacebar when a piece is selected)
  - Piece restoration (double-click on a used piece to remove it from the board)
  
- **`Board.js`** — Renders the board as a CSS Grid. Features:
  - Colour-codes pieces using HSL based on piece index: `hsl((pieceId * 40) % 360, 70%, 80%)`
  - Draws thick borders between different pieces (piece boundary detection)
  - Highlights target cells in red (`#ff3b3b`)
  - Shows hover preview when dragging a piece over the board
  - OFF_BOARD cells rendered as grey
  - Each cell is 60×60px

- **`PiecesList.js`** — Renders the 9 available pieces in a grid. Features:
  - Each piece rendered as a mini grid (20×20px cells) with its shape filled in matching colour
  - Click to select a piece, spacebar to rotate, Escape to deselect
  - Drag-and-drop support
  - Used pieces shown at 50% opacity; double-click to restore

#### Styling

- **`App.css`** — Layout: dark header bar, centred workspace card (board + pieces side by side), solve button
- **`Board.css`** — Grid cells: 60×60px, consistent borders, hover/filled state colours
- **`PiecesList.css`** — Responsive grid of piece cards with selection highlight

## The Puzzle Board

The board is a 7-row × 9-column irregular shape:

```
 Row 0:  .  Jan Feb Mar Apr May Jun  .   .
 Row 1:  .  Jul Aug Sep Oct Nov Dec  .   .
 Row 2:  .   1   2   3   4   5   6   7   .
 Row 3:  .   8   9  10  11  12  13  14   .
 Row 4:  .  15  16  17  18  19  20  21   .
 Row 5:  .  22  23  24  25  26  27  28   .
 Row 6:  .   .   .  29  30  31   .   .   .
```

- Cells marked `.` are `OFF_BOARD` (not part of the puzzle)
- The remaining 43 cells are `FILLABLE`
- When a date is selected, 2 cells become `TARGET` (left uncovered), leaving 41 cells to fill

## The 9 Puzzle Pieces

| # | ID | Shape | Size |
|---|---|---|---|
| 1 | L_small | Small L-shape | 4 cells |
| 2 | L_big | Large L-shape | 5 cells |
| 3 | S1 | 2×2 square | 4 cells |
| 4 | T1 | T-tetromino | 3 cells |
| 5 | Lightning | S/Z-tetromino | 4 cells |
| 6 | Bridge | U-shape (3×2 with gap) | 5 cells |
| 7 | LightningBig | Large S/Z-pentomino | 5 cells |
| 8 | SquarePlus | L-like pentomino | 5 cells |
| 9 | Cross | Plus/cross pentomino | 5 cells |

Total piece cells: 4+5+4+3+4+5+5+5+5 = 40. With 2 target cells removed from 43 fillable cells, that leaves 41 cells — so the solver needs to find an exact cover of those 41 cells. *(Note: The T1 piece as defined is actually 3 cells from 2 rows — verify if this is intentional or should be 4 cells.)*

## API Contract

### GET /api/board
Returns: `List<BoardCell>` — every cell in the grid (including OFF_BOARD), serialised as:
```json
[{ "row": 0, "col": 0, "label": null, "state": "OFF_BOARD" },
 { "row": 0, "col": 1, "label": "Jan", "state": "FILLABLE" }, ...]
```

### GET /api/pieces
Returns: `List<PieceDto>` — the 9 pieces with integer IDs:
```json
[{ "id": 0, "name": "L_small", "shape": [{"row":0,"col":0}, {"row":1,"col":0}, {"row":2,"col":0}, {"row":2,"col":1}] }, ...]
```

### POST /api/solve
Returns: `List<PlacementDto>` — the solution (one entry per piece placed):
```json
[{ "pieceId": 0, "cells": [{"row":2,"col":1}, {"row":3,"col":1}, {"row":4,"col":1}, {"row":4,"col":2}] }, ...]
```

### POST /api/updateTargetDate?date=YYYY-MM-DD
Body: form-encoded `date=YYYY-MM-DD`
Returns: `void` (200 OK). Resets the board and sets new target cells.

## Solving Algorithm

1. The board's fillable cells (excluding targets) are enumerated and mapped to column indices 0..N-1
2. Each piece's orientations (rotations + flips) are generated; for each orientation, every valid board position is tried → produces a list of `Placement` objects
3. An Exact Cover matrix is built: columns = fillable cells + one "usage" column per piece; rows = all valid placements
4. **Dancing Links (DLX)** solves the Exact Cover problem, finding a set of rows (placements) that covers every column exactly once — meaning every cell is filled and every piece is used exactly once
5. The solution rows are mapped back to `Placement` objects and returned via the API

## Development Notes

- **Integrated build**: `./gradlew build` builds both the React frontend and the Spring Boot backend into a single fat JAR. The Gradle `node` plugin (`com.github.node-gradle.node` v7.1.0) downloads Node.js 20, runs `npm install` and `npm run build` in `src/frontend/`, then copies the React build output into Spring Boot's static resources.
- **Development workflow**: For frontend-only development, you can still run the React dev server separately (`cd src/frontend && npm start`) on port 3000, which proxies API requests to the Spring Boot backend on port 8080 (configured via `"proxy": "http://localhost:8080"` in `package.json`).
- The backend runs on the default Spring Boot port (8080).
- There are currently **no tests** in the codebase.
- Board state is held in a single `SolverController` instance (Spring singleton). This means concurrent requests could conflict — the application is not designed for multi-user concurrent use.

## Running the Application

### Production (single JAR)
```bash
./gradlew clean build -x test
java -jar build/libs/WholeYearSolverAPI-1.0.0.jar
```
Builds the full application (React + Spring Boot) and starts it on `http://localhost:8080`. The React UI is served at `/` and the API at `/api/*`.

### Development (separate servers)
```bash
# Terminal 1: Start Spring Boot backend
./gradlew bootRun

# Terminal 2: Start React dev server with hot reload
cd src/frontend
npm install
npm start
```
React dev server on `http://localhost:3000` proxies API calls to `http://localhost:8080`.

## Key Design Decisions

- **Exact Cover formulation**: The puzzle is modelled as an exact cover problem where both cell coverage AND piece usage are encoded as columns, ensuring every piece is used exactly once.
- **Piece orientations**: Generated programmatically via rotation (90° clockwise) and horizontal flip, with deduplication by coordinate signature.
- **Board targeting**: Instead of removing cells, target date cells are set to `TARGET` state — they remain visible on the board (shown in red) but are excluded from the fillable set.
- **Manual + automated solving**: Users can either drag-and-drop pieces manually or click "Solve" for an automated solution.

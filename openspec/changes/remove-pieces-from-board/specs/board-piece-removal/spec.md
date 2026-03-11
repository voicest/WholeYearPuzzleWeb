## ADDED Requirements

### Requirement: Click placed piece on board to remove it
The system SHALL allow the user to click on any cell of a placed piece on the board to remove that piece entirely. When removed, the piece SHALL be returned to the available pieces list and all cells it occupied SHALL become empty fillable cells again.

#### Scenario: Remove a placed piece by clicking its cell
- **WHEN** the user clicks on a board cell occupied by a manually-placed or solver-placed piece
- **THEN** the entire piece is removed from the board, its cells become empty, and the piece is marked as available (not used) in the pieces list

#### Scenario: Click on an empty or target cell does nothing
- **WHEN** the user clicks on a board cell that is empty (FILLABLE with no piece), a TARGET cell, or an OFF_BOARD cell
- **THEN** nothing happens; the board state remains unchanged

### Requirement: Hover affordance on placed piece cells
The system SHALL display a visual hover indicator on board cells that belong to a placed piece, signalling that the cell is interactive and clickable.

#### Scenario: Hovering over a placed piece cell shows visual feedback
- **WHEN** the user hovers the mouse over a board cell occupied by a placed piece
- **THEN** the cell SHALL display a brightness change (filter) and the cursor SHALL change to a pointer

#### Scenario: Hovering over an empty cell shows no removal affordance
- **WHEN** the user hovers over an empty fillable cell, a target cell, or an off-board cell
- **THEN** no pointer cursor or brightness change is displayed for removal purposes

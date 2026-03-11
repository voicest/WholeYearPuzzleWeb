## ADDED Requirements

### Requirement: Clear all pieces from the board
The system SHALL provide a "Clear Board" button that removes all placed pieces from the board in one action, resetting ALL pieces to available (not used) and clearing the solution array.

#### Scenario: Clear board with placed pieces
- **WHEN** the user clicks the "Clear Board" button while one or more pieces are placed on the board
- **THEN** all pieces are removed from the board, all pieces are marked as available in the pieces list, and the board shows only empty fillable cells and target cells

#### Scenario: Clear board with no placed pieces
- **WHEN** the user clicks the "Clear Board" button while no pieces are placed on the board
- **THEN** nothing changes; the board remains in its current state

### Requirement: Clear Board button is disabled when board is empty
The system SHALL disable the "Clear Board" button when no pieces are placed on the board, providing a visual indication that there is nothing to clear.

#### Scenario: Button disabled state
- **WHEN** no pieces are placed on the board (solution is empty)
- **THEN** the "Clear Board" button SHALL be visually disabled and non-interactive

#### Scenario: Button enabled state
- **WHEN** at least one piece is placed on the board
- **THEN** the "Clear Board" button SHALL be visually enabled and clickable

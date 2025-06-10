package com.wholeyear.model;

import java.util.*;

/**
 * Board with an irregular shape, where each fillable cell also has an associated label (String).
 * 
 * Now supports multiple cell states via the CellState enum.
 */
public class Board {
    public enum CellState {
        OFF_BOARD,   // Cell does not exist
        FILLABLE, // Cell is fillable (can be filled by a piece)
        TARGET,   // Cell is not fillable and is the target to left visible
        BLOCKED      // Cell was fillable but is now blocked/punched out
    }

    private final int rows, cols;
    private final CellState[][] grid;
    private final String[][] labels;

    public Board(List<String> asciiShape, List<List<String>> cellLabels) {
        if (asciiShape == null || asciiShape.isEmpty()) {
            throw new IllegalArgumentException("asciiShape must be non-null and non-empty");
        }
        this.rows = asciiShape.size();
        this.cols = asciiShape.get(0).length();

        for (String row : asciiShape) {
            if (row.length() != this.cols) {
                throw new IllegalArgumentException("All asciiShape rows must be the same length");
            }
        }
        if (cellLabels.size() != rows) {
            throw new IllegalArgumentException("cellLabels must have exactly " + rows + " rows");
        }
        for (List<String> labelRow : cellLabels) {
            if (labelRow.size() != cols) {
                throw new IllegalArgumentException(
                    "Each row in cellLabels must have length = " + cols);
            }
        }

        this.grid = new CellState[rows][cols];
        this.labels = new String[rows][cols];

        for (int r = 0; r < rows; r++) {
            String line = asciiShape.get(r);
            for (int c = 0; c < cols; c++) {
                char ch = line.charAt(c);
                if (ch == '#') {
                    grid[r][c] = CellState.FILLABLE;
                    labels[r][c] = cellLabels.get(r).get(c);
                    if (labels[r][c] == null) {
                        throw new IllegalArgumentException(
                            "Label for a fillable cell (" + r + "," + c + ") cannot be null");
                    }
                } else {
                    grid[r][c] = CellState.OFF_BOARD;
                    labels[r][c] = cellLabels.get(r).get(c);
                }
            }
        }
    }

    /** Copy‐constructor (deep copy of grid + labels). */
    public Board(Board other) {
        this.rows = other.rows;
        this.cols = other.cols;
        this.grid = new CellState[rows][cols];
        this.labels = new String[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(other.labels[r], 0, this.labels[r], 0, cols);
            for (int c = 0; c < cols; c++) {
                this.grid[r][c] = other.grid[r][c];
            }
        }
    }

    /** Returns true if (r,c) is a valid index and cell is still fillable. */
    public boolean isFillable(int r, int c) {
        return r >= 0 
            && r < rows 
            && c >= 0 
            && c < cols 
            && grid[r][c] == CellState.FILLABLE;
    }

    /** Returns the label/value for (r,c).  May be null if off-board or if you set it that way. */
    public String getLabel(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            throw new IndexOutOfBoundsException("Invalid cell (" + r + "," + c + ")");
        }
        return labels[r][c];
    }

    public String getLabel(Cell cell) {
        return getLabel(cell.getRow(), cell.getCol());
    }

    /** 
     * “Punch out” (block) a single cell so pieces cannot occupy it anymore. 
     * After calling this, isFillable(r,c) will be false and state will be BLOCKED.
     */
    public void block(int r, int c) {
        
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            throw new IllegalStateException("Invalid cell (" + r + "," + c + ")");    
        }
        // Only block if the cell is currently fillable
        if (grid[r][c] == CellState.FILLABLE) {
            grid[r][c] = CellState.BLOCKED;
            //System.out.println("Blocked cell (" + r + "," + c + ").");
        } else if (grid[r][c] == CellState.TARGET) {
            // If it was a target, you cannot block it
            throw new IllegalStateException(
                "Cannot block a target cell (" + r + "," + c + "). Use setTarget() to mark it as a target.");
        } else if (grid[r][c] == CellState.OFF_BOARD) {
            //Cannot block an off-board cell
            throw new IllegalStateException(
                "Cannot block an off-board cell (" + r + "," + c + ").");
        } else if (grid[r][c] == CellState.BLOCKED) {
            // Already blocked, cannot block again
            throw new IllegalStateException(
                "Cell (" + r + "," + c + ") is already blocked.");
        }

    }   
        
    /**
     * Set a call as the target to be left visible.
     * After calling isfillable(r,c) will be false and state will be TARGET.
     * This is useful for marking cells that should remain visible after filling.
     */
    public void setTarget(int r, int c) {
        if (r >= 0 && r < rows && c >= 0 && c < cols && grid[r][c] == CellState.FILLABLE) {
            grid[r][c] = CellState.TARGET;
        } else {
            throw new IllegalStateException("Cannot set target on invalid or non-fillable cell (" + r + "," + c + ")");
        }
    }


    /** 
     * Given a relative‐shape (list of Cells with small coords), block them 
     * at (originRow + relRow, originCol + relCol).  
     * If any of those falls off‐board, we silently ignore it.
     */
    public void blockShape(List<Cell> shapeCells, int originRow, int originCol) {
        for (Cell rel : shapeCells) {
            int rr = originRow + rel.getRow();
            int cc = originCol + rel.getCol();
            if (rr >= 0 && rr < rows && cc >= 0 && cc < cols && grid[rr][cc] == CellState.FILLABLE) {
                grid[rr][cc] = CellState.BLOCKED;
            }
        }
    }

    /**
     * Return a list of all currently‐fillable cells (row‐major).  You can then call getLabel(...) 
     * on each cell to retrieve its value.
     */
    public List<Cell> getAllFillableCells() {
        List<Cell> result = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == CellState.FILLABLE) {
                    result.add(new Cell(r, c));
                }
            }
        }
        return result;
    }

    /** 
     * Print the board to console, using:
     *   - ‘#’ for fillable cells  
     *   - ‘X’ for blocked cells  
     *   - ‘.’ for off‐board cells  
     */
    public void printShape() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                switch (grid[r][c]) {
                    case FILLABLE: System.out.print("#"); break;
                    case BLOCKED: System.out.print("X"); break;
                    case OFF_BOARD: default: System.out.print("."); break;
                }
            }
            System.out.println();
        }
    }

    /** 
     * Print all fillable cells with their labels, e.g. 
     * “(r,c) → LABEL”. Useful for debugging. 
     */
    public void printLabels() {
        System.out.println("Fillable cells and their labels:");
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == CellState.FILLABLE) {
                    System.out.printf(" (%d,%d) = \"%s\"\n", r, c, labels[r][c]);
                }
            }
        }
    }

    /**
     * Search the entire board for a cell whose label equals `targetLabel`.
     * If found, returns a new Cell(r,c). If no such label exists, returns null.
     */
    public Cell findCellByLabel(String targetLabel) {
        if (targetLabel == null) return null;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == CellState.FILLABLE) {
                    String lbl = labels[r][c];
                    if (targetLabel.equals(lbl)) {
                        return new Cell(r, c);
                    }
                }
            }
        }
        return null;
    }

    public void reset() {

        // resent target cells to fillable
        // Reset all cells to fillable, except those that were blocked 
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == CellState.BLOCKED  || grid[r][c] == CellState.OFF_BOARD) {
                    continue; // Keep blocked and target cells as is
                } else {
                    grid[r][c] = CellState.FILLABLE; // Reset to fillable
                }
            }
        }
    }

    // Getters for dimensions if needed:
    public int getRows() { return rows; }
    public int getCols() { return cols; }

    /** Returns the CellState for a given cell. */
    public CellState getCellState(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            throw new IndexOutOfBoundsException("Invalid cell (" + r + "," + c + ")");
        }
        return grid[r][c];
    }
}
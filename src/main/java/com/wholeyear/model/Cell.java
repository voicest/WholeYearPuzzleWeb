package com.wholeyear.model;

/** 
 * Immutable helper representing a coordinate on the board.
 * (We do not store the “label” here—labels are kept in Board.)
 */
public class Cell {
    private final int row;
    private final int col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public int getRow() { return row; }
    public int getCol() { return col; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell other = (Cell) o;
        return row == other.row && col == other.col;
    }
    @Override
    public int hashCode() {
        return 31 * row + col;
    }
    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}

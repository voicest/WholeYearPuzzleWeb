package com.wholeyear.model;

public class BoardCell {
    private int row;
    private int col;
    private String label;

    // default constructor (for Jackson)
    public BoardCell() { }

    public BoardCell(int row, int col, String label) {
        this.row = row;
        this.col = col;
        this.label = label;
    }

    // getters + setters
    public int getRow()       { return row; }
    public void setRow(int r) { this.row = r; }

    public int getCol()         { return col; }
    public void setCol(int c)   { this.col = c; }

    public String getLabel()         { return label; }
    public void setLabel(String lab) { this.label = lab; }
}
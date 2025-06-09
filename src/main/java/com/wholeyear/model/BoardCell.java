package com.wholeyear.model;

import com.wholeyear.model.Board.CellState;

public class BoardCell {
    private int row;
    private int col;
    private String label;
    private CellState state;

    // default constructor (for Jackson)
    public BoardCell() { }

    public BoardCell(int row, int col, String label, CellState state) {
        this.row = row;
        this.col = col;
        this.label = label;
        this.state = state; // default state
    }

    // getters + setters
    public int getRow()       { return row; }
    public void setRow(int r) { this.row = r; }

    public int getCol()         { return col; }
    public void setCol(int c)   { this.col = c; }

    public String getLabel()         { return label; }
    public void setLabel(String lab) { this.label = lab; }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }




}
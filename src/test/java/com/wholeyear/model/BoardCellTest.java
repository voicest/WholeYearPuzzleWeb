package com.wholeyear.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardCellTest {

    @Test
    void constructorAndGetters() {
        BoardCell bc = new BoardCell(2, 3, "Jan", Board.CellState.FILLABLE);
        assertEquals(2, bc.getRow());
        assertEquals(3, bc.getCol());
        assertEquals("Jan", bc.getLabel());
        assertEquals(Board.CellState.FILLABLE, bc.getState());
    }

    @Test
    void setters() {
        BoardCell bc = new BoardCell();
        bc.setRow(1);
        bc.setCol(4);
        bc.setLabel("Feb");
        bc.setState(Board.CellState.TARGET);
        assertEquals(1, bc.getRow());
        assertEquals(4, bc.getCol());
        assertEquals("Feb", bc.getLabel());
        assertEquals(Board.CellState.TARGET, bc.getState());
    }

    @Test
    void defaultConstructorFieldsAreDefaults() {
        BoardCell bc = new BoardCell();
        assertEquals(0, bc.getRow());
        assertEquals(0, bc.getCol());
        assertNull(bc.getLabel());
        assertNull(bc.getState());
    }
}

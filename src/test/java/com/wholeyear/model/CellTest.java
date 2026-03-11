package com.wholeyear.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void constructorAndGetters() {
        Cell cell = new Cell(3, 7);
        assertEquals(3, cell.getRow());
        assertEquals(7, cell.getCol());
    }

    @Test
    void equalsSameCoordinates() {
        Cell a = new Cell(2, 5);
        Cell b = new Cell(2, 5);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentCoordinates() {
        Cell a = new Cell(0, 0);
        Cell b = new Cell(0, 1);
        assertNotEquals(a, b);
    }

    @Test
    void equalsWithNull() {
        Cell cell = new Cell(1, 1);
        assertNotEquals(null, cell);
    }

    @Test
    void toStringFormat() {
        Cell cell = new Cell(4, 8);
        assertEquals("(4,8)", cell.toString());
    }
}

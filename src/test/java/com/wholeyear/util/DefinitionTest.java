package com.wholeyear.util;

import com.wholeyear.model.Board;
import com.wholeyear.model.Cell;
import com.wholeyear.model.Piece;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DefinitionTest {

    @Test
    void loadAllPiecesReturnsNinePieces() {
        List<Piece> pieces = Definition.loadAllPieces();
        assertEquals(9, pieces.size());
    }

    @Test
    void allPieceIdsAreUnique() {
        List<Piece> pieces = Definition.loadAllPieces();
        Set<String> ids = new HashSet<>();
        for (Piece p : pieces) {
            assertTrue(ids.add(p.getId()), "Duplicate piece ID: " + p.getId());
        }
    }

    @Test
    void allPieceIdsMatchExpected() {
        List<Piece> pieces = Definition.loadAllPieces();
        Set<String> ids = new HashSet<>();
        for (Piece p : pieces) {
            ids.add(p.getId());
        }
        assertTrue(ids.contains("L_small"));
        assertTrue(ids.contains("L_big"));
        assertTrue(ids.contains("S1"));
        assertTrue(ids.contains("T1"));
        assertTrue(ids.contains("Lightning"));
        assertTrue(ids.contains("Bridge"));
        assertTrue(ids.contains("LightningBig"));
        assertTrue(ids.contains("SquarePlus"));
        assertTrue(ids.contains("Cross"));
    }

    @Test
    void totalPieceCellsEquals40() {
        List<Piece> pieces = Definition.loadAllPieces();
        int totalCells = pieces.stream()
                .mapToInt(p -> p.getCanonicalCells().size())
                .sum();
        // With 43 fillable cells and 2 targets, we need 41 cells covered.
        // The 9 pieces total 41 cells.
        assertEquals(41, totalCells);
    }

    @Test
    void individualPieceSizes() {
        List<Piece> pieces = Definition.loadAllPieces();
        // Expected sizes: L_small=4, L_big=5, S1=4, T1=4(?), Lightning=4, Bridge=5, LightningBig=5, SquarePlus=5, Cross=5
        for (Piece p : pieces) {
            int size = p.getCanonicalCells().size();
            assertTrue(size >= 3 && size <= 5,
                    "Piece " + p.getId() + " has unexpected size: " + size);
        }
    }

    // ─── Board creation tests ───────────────────────────────────────────

    @Test
    void createWholeYearPuzzleBoardNotNull() {
        Board board = Definition.createWholeYearPuzzleBoard();
        assertNotNull(board);
    }

    @Test
    void wholeYearBoardDimensions() {
        Board board = Definition.createWholeYearPuzzleBoard();
        assertEquals(7, board.getRows());
        assertEquals(9, board.getCols());
    }

    @Test
    void wholeYearBoardHas43FillableCells() {
        Board board = Definition.createWholeYearPuzzleBoard();
        List<Cell> fillable = board.getAllFillableCells();
        assertEquals(43, fillable.size());
    }

    @Test
    void wholeYearBoardContainsAllMonths() {
        Board board = Definition.createWholeYearPuzzleBoard();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month : months) {
            assertNotNull(board.findCellByLabel(month),
                    "Board should contain month: " + month);
        }
    }

    @Test
    void wholeYearBoardContainsAllDays() {
        Board board = Definition.createWholeYearPuzzleBoard();
        for (int day = 1; day <= 31; day++) {
            assertNotNull(board.findCellByLabel(String.valueOf(day)),
                    "Board should contain day: " + day);
        }
    }

    @Test
    void wholeYearBoardMonthPositions() {
        Board board = Definition.createWholeYearPuzzleBoard();
        // Row 0: Jan–Jun (cols 1–6)
        assertEquals("Jan", board.getLabel(0, 1));
        assertEquals("Jun", board.getLabel(0, 6));
        // Row 1: Jul–Dec (cols 1–6)
        assertEquals("Jul", board.getLabel(1, 1));
        assertEquals("Dec", board.getLabel(1, 6));
    }

    @Test
    void wholeYearBoardDayPositions() {
        Board board = Definition.createWholeYearPuzzleBoard();
        // Row 2: days 1–7 (cols 1–7)
        assertEquals("1", board.getLabel(2, 1));
        assertEquals("7", board.getLabel(2, 7));
        // Row 6: days 29–31 (cols 3–5)
        assertEquals("29", board.getLabel(6, 3));
        assertEquals("31", board.getLabel(6, 5));
    }

    @Test
    void wholeYearBoardCornersAreOffBoard() {
        Board board = Definition.createWholeYearPuzzleBoard();
        assertEquals(Board.CellState.OFF_BOARD, board.getCellState(0, 0));
        assertEquals(Board.CellState.OFF_BOARD, board.getCellState(0, 7));
        assertEquals(Board.CellState.OFF_BOARD, board.getCellState(0, 8));
        assertEquals(Board.CellState.OFF_BOARD, board.getCellState(6, 0));
        assertEquals(Board.CellState.OFF_BOARD, board.getCellState(6, 1));
        assertEquals(Board.CellState.OFF_BOARD, board.getCellState(6, 6));
    }

    // ─── createBoard validation tests ───────────────────────────────────

    @Test
    void createBoardRejectsNullShape() {
        List<List<String>> labels = new ArrayList<>();
        labels.add(Arrays.asList("A"));
        assertThrows(IllegalArgumentException.class,
                () -> Definition.createBoard(null, labels));
    }

    @Test
    void createBoardRejectsNullLabels() {
        assertThrows(IllegalArgumentException.class,
                () -> Definition.createBoard(Arrays.asList("#"), null));
    }

    @Test
    void createBoardRejectsMismatchedRowCount() {
        List<List<String>> labels = new ArrayList<>();
        labels.add(Arrays.asList("A"));
        labels.add(Arrays.asList("B"));
        assertThrows(IllegalArgumentException.class,
                () -> Definition.createBoard(Arrays.asList("#"), labels));
    }

    @Test
    void createBoardValidSingleCell() {
        List<List<String>> labels = new ArrayList<>();
        labels.add(Arrays.asList("X"));
        Board board = Definition.createBoard(Arrays.asList("#"), labels);
        assertNotNull(board);
        assertEquals(1, board.getRows());
        assertEquals(1, board.getCols());
        assertEquals("X", board.getLabel(0, 0));
    }
}

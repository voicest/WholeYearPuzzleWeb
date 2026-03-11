package com.wholeyear.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    /**
     * Creates a simple 3×3 board:
     *   .#.
     *   ###
     *   .#.
     * Labels: null, A, null / B, C, D / null, E, null
     */
    @BeforeEach
    void setUp() {
        List<String> shape = Arrays.asList(
                ".#.",
                "###",
                ".#."
        );
        List<List<String>> labels = Arrays.asList(
                Arrays.asList(null, "A", null),
                Arrays.asList("B", "C", "D"),
                Arrays.asList(null, "E", null)
        );
        board = new Board(shape, labels);
    }

    @Test
    void dimensions() {
        assertEquals(3, board.getRows());
        assertEquals(3, board.getCols());
    }

    @Test
    void fillableCells() {
        // (0,1), (1,0), (1,1), (1,2), (2,1) = 5 fillable cells
        assertTrue(board.isFillable(0, 1));
        assertTrue(board.isFillable(1, 0));
        assertTrue(board.isFillable(1, 1));
        assertTrue(board.isFillable(1, 2));
        assertTrue(board.isFillable(2, 1));
    }

    @Test
    void offBoardCellsNotFillable() {
        assertFalse(board.isFillable(0, 0));
        assertFalse(board.isFillable(0, 2));
        assertFalse(board.isFillable(2, 0));
        assertFalse(board.isFillable(2, 2));
    }

    @Test
    void outOfBoundsNotFillable() {
        assertFalse(board.isFillable(-1, 0));
        assertFalse(board.isFillable(0, -1));
        assertFalse(board.isFillable(3, 0));
        assertFalse(board.isFillable(0, 3));
    }

    @Test
    void labels() {
        assertEquals("A", board.getLabel(0, 1));
        assertEquals("C", board.getLabel(1, 1));
        assertNull(board.getLabel(0, 0)); // OFF_BOARD cell
    }

    @Test
    void getLabelByCell() {
        assertEquals("C", board.getLabel(new Cell(1, 1)));
    }

    @Test
    void getLabelOutOfBoundsThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getLabel(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> board.getLabel(0, 5));
    }

    @Test
    void getCellState() {
        assertEquals(Board.CellState.OFF_BOARD, board.getCellState(0, 0));
        assertEquals(Board.CellState.FILLABLE, board.getCellState(1, 1));
    }

    @Test
    void getCellStateOutOfBoundsThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCellState(-1, 0));
    }

    @Test
    void getAllFillableCells() {
        List<Cell> fillable = board.getAllFillableCells();
        assertEquals(5, fillable.size());
        assertTrue(fillable.contains(new Cell(0, 1)));
        assertTrue(fillable.contains(new Cell(1, 0)));
        assertTrue(fillable.contains(new Cell(1, 1)));
        assertTrue(fillable.contains(new Cell(1, 2)));
        assertTrue(fillable.contains(new Cell(2, 1)));
    }

    // ─── Block tests ────────────────────────────────────────────────────

    @Test
    void blockMakesCellNotFillable() {
        assertTrue(board.isFillable(1, 1));
        board.block(1, 1);
        assertFalse(board.isFillable(1, 1));
        assertEquals(Board.CellState.BLOCKED, board.getCellState(1, 1));
    }

    @Test
    void blockOffBoardThrows() {
        assertThrows(IllegalStateException.class, () -> board.block(0, 0));
    }

    @Test
    void blockAlreadyBlockedThrows() {
        board.block(1, 1);
        assertThrows(IllegalStateException.class, () -> board.block(1, 1));
    }

    @Test
    void blockInvalidCoordinatesThrows() {
        assertThrows(IllegalStateException.class, () -> board.block(-1, 0));
    }

    // ─── Target tests ───────────────────────────────────────────────────

    @Test
    void setTargetMakesCellNotFillable() {
        assertTrue(board.isFillable(1, 1));
        board.setTarget(1, 1);
        assertFalse(board.isFillable(1, 1));
        assertEquals(Board.CellState.TARGET, board.getCellState(1, 1));
    }

    @Test
    void setTargetOnOffBoardThrows() {
        assertThrows(IllegalStateException.class, () -> board.setTarget(0, 0));
    }

    @Test
    void blockTargetCellThrows() {
        board.setTarget(1, 1);
        assertThrows(IllegalStateException.class, () -> board.block(1, 1));
    }

    // ─── Reset tests ────────────────────────────────────────────────────

    @Test
    void resetRestoresTargetCellsToFillable() {
        board.setTarget(1, 1);
        assertFalse(board.isFillable(1, 1));
        board.reset();
        assertTrue(board.isFillable(1, 1));
    }

    @Test
    void resetKeepsBlockedCellsBlocked() {
        board.block(1, 0);
        board.reset();
        assertFalse(board.isFillable(1, 0));
        assertEquals(Board.CellState.BLOCKED, board.getCellState(1, 0));
    }

    @Test
    void resetKeepsOffBoardCellsOffBoard() {
        board.reset();
        assertEquals(Board.CellState.OFF_BOARD, board.getCellState(0, 0));
    }

    // ─── findCellByLabel tests ──────────────────────────────────────────

    @Test
    void findCellByLabelFound() {
        Cell found = board.findCellByLabel("C");
        assertNotNull(found);
        assertEquals(1, found.getRow());
        assertEquals(1, found.getCol());
    }

    @Test
    void findCellByLabelNotFound() {
        assertNull(board.findCellByLabel("Z"));
    }

    @Test
    void findCellByLabelNull() {
        assertNull(board.findCellByLabel(null));
    }

    @Test
    void findCellByLabelDoesNotFindTargetCell() {
        board.setTarget(1, 1); // Label "C" cell becomes TARGET
        assertNull(board.findCellByLabel("C")); // findCellByLabel only checks FILLABLE cells
    }

    // ─── blockShape tests ───────────────────────────────────────────────

    @Test
    void blockShapeBlocksMultipleCells() {
        List<Cell> shape = Arrays.asList(new Cell(0, 0), new Cell(1, 0));
        board.blockShape(shape, 1, 0); // blocks (1,0) and (2,0) — but (2,0) is OFF_BOARD
        assertFalse(board.isFillable(1, 0));
        // (2,0) is OFF_BOARD, so blockShape silently ignores it
        assertEquals(Board.CellState.OFF_BOARD, board.getCellState(2, 0));
    }

    // ─── Copy constructor tests ─────────────────────────────────────────

    @Test
    void copyConstructorCreatesIndependentCopy() {
        Board copy = new Board(board);
        assertEquals(board.getRows(), copy.getRows());
        assertEquals(board.getCols(), copy.getCols());
        assertEquals("C", copy.getLabel(1, 1));

        // Modifying original shouldn't affect copy
        board.block(1, 1);
        assertTrue(copy.isFillable(1, 1));
    }

    // ─── Constructor validation tests ───────────────────────────────────

    @Test
    void constructorRejectsNullShape() {
        assertThrows(IllegalArgumentException.class, () ->
                new Board(null, Arrays.asList(Arrays.asList("A"))));
    }

    @Test
    void constructorRejectsEmptyShape() {
        assertThrows(IllegalArgumentException.class, () ->
                new Board(Arrays.asList(), Arrays.asList()));
    }

    @Test
    void constructorRejectsUnequalRowLengths() {
        assertThrows(IllegalArgumentException.class, () ->
                new Board(Arrays.asList("##", "#"),
                        Arrays.asList(Arrays.asList("A", "B"), Arrays.asList("C"))));
    }

    @Test
    void constructorRejectsMismatchedLabelRows() {
        List<String> shape = Arrays.asList("##");
        List<List<String>> labels = Arrays.asList(
                Arrays.asList("A", "B"),
                Arrays.asList("C", "D") // extra row
        );
        assertThrows(IllegalArgumentException.class, () -> new Board(shape, labels));
    }

    @Test
    void constructorRejectsNullLabelOnFillableCell() {
        List<String> shape = Arrays.asList("##");
        List<List<String>> labels = Arrays.asList(Arrays.asList("A", null));
        assertThrows(IllegalArgumentException.class, () -> new Board(shape, labels));
    }
}

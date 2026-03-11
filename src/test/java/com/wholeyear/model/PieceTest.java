package com.wholeyear.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

    @Test
    void constructorParsesAsciiTemplate() {
        // L-shape: #.
        //          #.
        //          ##
        Piece piece = new Piece("L", Arrays.asList("#.", "#.", "##"));
        assertEquals("L", piece.getId());
        assertEquals(4, piece.getCanonicalCells().size());
        assertEquals(3, piece.getOriginalHeight());
        assertEquals(2, piece.getOriginalWidth());
    }

    @Test
    void canonicalCellsAreNormalized() {
        // Shape:  .#
        //         .#
        Piece piece = new Piece("bar", Arrays.asList(".#", ".#"));
        List<Cell> cells = piece.getCanonicalCells();
        assertEquals(2, cells.size());
        // Should be normalized to (0,0), (1,0)
        assertTrue(cells.contains(new Cell(0, 0)));
        assertTrue(cells.contains(new Cell(1, 0)));
    }

    @Test
    void canonicalCellsAreUnmodifiable() {
        Piece piece = new Piece("test", Arrays.asList("##"));
        assertThrows(UnsupportedOperationException.class,
                () -> piece.getCanonicalCells().add(new Cell(0, 0)));
    }

    @Test
    void constructorRejectsEmptyPiece() {
        assertThrows(IllegalArgumentException.class,
                () -> new Piece("empty", Arrays.asList("..", "..")));
    }

    @Test
    void constructorRejectsUnequalRowLengths() {
        assertThrows(IllegalArgumentException.class,
                () -> new Piece("bad", Arrays.asList("#", "##")));
    }

    // ─── Orientation generation tests ───────────────────────────────────

    @Test
    void squareHasOneOrientation() {
        // 2x2 square is identical in all rotations/flips
        Piece square = new Piece("sq", Arrays.asList("##", "##"));
        List<List<Cell>> orientations = square.generateAllOrientations();
        assertEquals(1, orientations.size());
    }

    @Test
    void lineHasTwoOrientations() {
        // Horizontal line: ##
        Piece line = new Piece("line", Arrays.asList("##"));
        List<List<Cell>> orientations = line.generateAllOrientations();
        assertEquals(2, orientations.size());
    }

    @Test
    void lShapeHasEightOrientations() {
        // L-shape is asymmetric: 4 rotations × 2 flips = 8 distinct orientations
        Piece lPiece = new Piece("L", Arrays.asList("#.", "#.", "##"));
        List<List<Cell>> orientations = lPiece.generateAllOrientations();
        assertEquals(8, orientations.size());
    }

    @Test
    void crossHasOneOrientation() {
        // Plus/cross shape is identical in all orientations
        Piece cross = new Piece("cross", Arrays.asList(".#.", "###", ".#."));
        List<List<Cell>> orientations = cross.generateAllOrientations();
        assertEquals(1, orientations.size());
    }

    @Test
    void sZTetrominoHasFourOrientations() {
        // S/Z tetromino: flip produces distinct Z shape, each with 2 rotations = 4
        Piece sz = new Piece("sz", Arrays.asList(".##", "##."));
        List<List<Cell>> orientations = sz.generateAllOrientations();
        assertEquals(4, orientations.size());
    }

    @Test
    void allOrientationsHaveSameCellCount() {
        Piece piece = new Piece("L", Arrays.asList("#.", "#.", "##"));
        int expectedSize = piece.getCanonicalCells().size();
        for (List<Cell> orientation : piece.generateAllOrientations()) {
            assertEquals(expectedSize, orientation.size(),
                    "Every orientation should have the same number of cells");
        }
    }

    @Test
    void allOrientationsAreNormalized() {
        Piece piece = new Piece("bridge", Arrays.asList("###", "#.#"));
        for (List<Cell> orientation : piece.generateAllOrientations()) {
            int minR = orientation.stream().mapToInt(Cell::getRow).min().orElse(0);
            int minC = orientation.stream().mapToInt(Cell::getCol).min().orElse(0);
            assertEquals(0, minR, "min row should be 0 after normalization");
            assertEquals(0, minC, "min col should be 0 after normalization");
        }
    }
}

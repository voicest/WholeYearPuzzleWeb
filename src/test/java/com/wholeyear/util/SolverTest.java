package com.wholeyear.util;

import com.wholeyear.model.Board;
import com.wholeyear.model.Cell;
import com.wholeyear.model.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {

    // ─── Helper: build a small 2×3 board to use with simple pieces ─────
    private Board createSmallBoard() {
        // A 2×3 fully-fillable board:
        //   ###
        //   ###
        List<String> shape = Arrays.asList("###", "###");
        List<List<String>> labels = Arrays.asList(
                Arrays.asList("A", "B", "C"),
                Arrays.asList("D", "E", "F"));
        return Definition.createBoard(shape, labels);
    }

    // ─── Helper: pieces that exactly fill a 2×3 board ──────────────────
    // Two pieces: a 2×2 square + a 2×1 domino
    private List<Piece> smallPieceSet() {
        List<Piece> pieces = new ArrayList<>();
        // 2×2 square (4 cells)
        pieces.add(new Piece("sq", Arrays.asList("##", "##")));
        // 2×1 vertical domino (2 cells)
        pieces.add(new Piece("dom", Arrays.asList("#", "#")));
        return pieces;
    }

    @Test
    void solveSmallBoardReturnsValidSolution() {
        Board board = createSmallBoard();
        List<Piece> pieces = smallPieceSet();
        Solver solver = new Solver(board, pieces);

        List<Placement> solution = solver.solve();
        assertNotNull(solution, "Solver should find a solution for 2×3 board with sq + dom");
        assertEquals(2, solution.size(), "Solution should use exactly 2 pieces");

        // Collect all covered cells
        Set<Cell> covered = new HashSet<>();
        for (Placement p : solution) {
            for (Cell c : p.getCoveredCells()) {
                assertFalse(covered.contains(c),
                        "Cell " + c + " covered more than once");
                covered.add(c);
            }
        }
        // All 6 cells should be covered
        assertEquals(6, covered.size());
    }

    @Test
    void solveSmallBoardUsesAllPieces() {
        Board board = createSmallBoard();
        List<Piece> pieces = smallPieceSet();
        Solver solver = new Solver(board, pieces);

        List<Placement> solution = solver.solve();
        assertNotNull(solution);

        Set<String> usedPieceIds = new HashSet<>();
        for (Placement p : solution) {
            usedPieceIds.add(p.getPieceId());
        }
        assertEquals(Set.of("sq", "dom"), usedPieceIds);
    }

    @Test
    void solveReturnsNullWhenNoSolutionPossible() {
        // Board: 3 cells in a row, but we only give a 2×2 square piece (4 cells)
        Board board = Definition.createBoard(
                Arrays.asList("###"),
                Arrays.asList(Arrays.asList("A", "B", "C")));
        List<Piece> pieces = List.of(new Piece("sq", Arrays.asList("##", "##")));

        Solver solver = new Solver(board, pieces);
        List<Placement> solution = solver.solve();
        assertNull(solution, "Should return null when no exact cover exists");
    }

    @Test
    void generateAllPlacementsNonEmpty() {
        Board board = createSmallBoard();
        List<Piece> pieces = smallPieceSet();
        Solver solver = new Solver(board, pieces);

        List<Placement> placements = solver.generateAllPlacements();
        assertFalse(placements.isEmpty(),
                "Should generate at least one placement for the small board");
    }

    @Test
    void generateAllPlacementsContainsBothPieces() {
        Board board = createSmallBoard();
        List<Piece> pieces = smallPieceSet();
        Solver solver = new Solver(board, pieces);

        List<Placement> placements = solver.generateAllPlacements();
        Set<String> pieceIds = new HashSet<>();
        for (Placement p : placements) {
            pieceIds.add(p.getPieceId());
        }
        assertTrue(pieceIds.contains("sq"));
        assertTrue(pieceIds.contains("dom"));
    }

    @Test
    void placementsOnlyCoverFillableCells() {
        Board board = createSmallBoard();
        // Block one cell to make it non-fillable
        board.block(0, 0);
        List<Piece> pieces = List.of(new Piece("dom", Arrays.asList("#", "#")));
        Solver solver = new Solver(board, pieces);

        List<Placement> placements = solver.generateAllPlacements();
        for (Placement p : placements) {
            for (Cell c : p.getCoveredCells()) {
                assertFalse(c.getRow() == 0 && c.getCol() == 0,
                        "No placement should cover blocked cell (0,0)");
            }
        }
    }

    // ─── Full board solve test ──────────────────────────────────────────

    @Test
    void solveWholeYearBoardForJan1() {
        Board board = Definition.createWholeYearPuzzleBoard();
        // Set Jan and 1 as targets
        Cell jan = board.findCellByLabel("Jan");
        Cell day1 = board.findCellByLabel("1");
        assertNotNull(jan);
        assertNotNull(day1);
        board.setTarget(jan.getRow(), jan.getCol());
        board.setTarget(day1.getRow(), day1.getCol());

        List<Piece> pieces = Definition.loadAllPieces();
        Solver solver = new Solver(board, pieces);
        List<Placement> solution = solver.solve();

        assertNotNull(solution, "Should find a solution for Jan 1");
        assertEquals(9, solution.size(), "Should use all 9 pieces");

        // Verify all fillable cells are covered exactly once
        Set<Cell> fillable = new HashSet<>(board.getAllFillableCells());
        Set<Cell> covered = new HashSet<>();
        for (Placement p : solution) {
            for (Cell c : p.getCoveredCells()) {
                assertTrue(fillable.contains(c),
                        "Placed cell " + c + " should be a fillable cell");
                assertTrue(covered.add(c),
                        "Cell " + c + " should not be covered twice");
            }
        }
        assertEquals(fillable.size(), covered.size(),
                "Every fillable cell should be covered");
    }

    @Test
    void solveWholeYearBoardForDec25() {
        Board board = Definition.createWholeYearPuzzleBoard();
        Cell dec = board.findCellByLabel("Dec");
        Cell day25 = board.findCellByLabel("25");
        assertNotNull(dec);
        assertNotNull(day25);
        board.setTarget(dec.getRow(), dec.getCol());
        board.setTarget(day25.getRow(), day25.getCol());

        List<Piece> pieces = Definition.loadAllPieces();
        Solver solver = new Solver(board, pieces);
        List<Placement> solution = solver.solve();

        assertNotNull(solution, "Should find a solution for Dec 25");
        assertEquals(9, solution.size(), "Should use all 9 pieces");
    }
}

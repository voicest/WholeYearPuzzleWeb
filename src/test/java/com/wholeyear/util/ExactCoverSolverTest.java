package com.wholeyear.util;

import com.wholeyear.model.Cell;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DLX ExactCoverSolver, independent of the puzzle domain.
 */
class ExactCoverSolverTest {

    private Placement dummyPlacement(String id) {
        return new Placement(id, 0, 0, 0, List.of(new Cell(0, 0)));
    }

    /**
     * Classic exact-cover example:
     *   Columns: 0 1 2 3 4 5 6
     *   Row A: 1 0 0 1 0 0 1
     *   Row B: 1 0 0 1 0 0 0
     *   Row C: 0 0 0 1 1 0 1
     *   Row D: 0 0 1 0 1 1 0
     *   Row E: 0 1 1 0 0 1 1
     *   Row F: 0 1 0 0 0 0 1
     *
     * Solution: rows B, D, F (covering all 7 columns exactly once)
     */
    @Test
    void solvesClassicExactCoverProblem() {
        int numCols = 7;
        List<boolean[]> rows = new ArrayList<>();
        rows.add(new boolean[]{true, false, false, true, false, false, true});  // A
        rows.add(new boolean[]{true, false, false, true, false, false, false}); // B
        rows.add(new boolean[]{false, false, false, true, true, false, true});  // C
        rows.add(new boolean[]{false, false, true, false, true, true, false});  // D
        rows.add(new boolean[]{false, true, true, false, false, true, true});   // E
        rows.add(new boolean[]{false, true, false, false, false, false, true}); // F

        List<Placement> placements = List.of(
                dummyPlacement("A"), dummyPlacement("B"), dummyPlacement("C"),
                dummyPlacement("D"), dummyPlacement("E"), dummyPlacement("F"));

        ExactCoverSolver solver = new ExactCoverSolver(numCols, rows, placements);
        List<Integer> result = solver.solve();

        assertNotNull(result, "Should find a solution");

        // Verify the solution covers all columns exactly once
        boolean[] covered = new boolean[numCols];
        for (int rowIdx : result) {
            boolean[] row = rows.get(rowIdx);
            for (int c = 0; c < numCols; c++) {
                if (row[c]) {
                    assertFalse(covered[c], "Column " + c + " covered twice");
                    covered[c] = true;
                }
            }
        }
        for (int c = 0; c < numCols; c++) {
            assertTrue(covered[c], "Column " + c + " not covered");
        }
    }

    /**
     * Trivial: single row that covers the only column.
     */
    @Test
    void solvesTrivialSingleColumnSingleRow() {
        List<boolean[]> rows = List.of(new boolean[]{true});
        List<Placement> placements = List.of(dummyPlacement("X"));

        ExactCoverSolver solver = new ExactCoverSolver(1, rows, placements);
        List<Integer> result = solver.solve();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0));
    }

    /**
     * No solution possible: two columns, one row that only covers the first.
     */
    @Test
    void returnsNullWhenNoSolution() {
        List<boolean[]> rows = List.of(new boolean[]{true, false});
        List<Placement> placements = List.of(dummyPlacement("X"));

        ExactCoverSolver solver = new ExactCoverSolver(2, rows, placements);
        List<Integer> result = solver.solve();

        assertNull(result, "Should return null when no exact cover exists");
    }

    /**
     * Identity matrix: N rows, N columns, each row covers exactly one column.
     * The solution should include all N rows.
     */
    @Test
    void solvesIdentityMatrix() {
        int n = 5;
        List<boolean[]> rows = new ArrayList<>();
        List<Placement> placements = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            boolean[] row = new boolean[n];
            row[i] = true;
            rows.add(row);
            placements.add(dummyPlacement("R" + i));
        }

        ExactCoverSolver solver = new ExactCoverSolver(n, rows, placements);
        List<Integer> result = solver.solve();

        assertNotNull(result);
        assertEquals(n, result.size());
        // All row indices should be present
        Set<Integer> indices = new HashSet<>(result);
        for (int i = 0; i < n; i++) {
            assertTrue(indices.contains(i), "Row " + i + " should be in solution");
        }
    }

    /**
     * Two disjoint rows that together cover all columns.
     * Row 0: columns 0,1
     * Row 1: columns 2,3
     */
    @Test
    void solvesDisjointPairCover() {
        List<boolean[]> rows = List.of(
                new boolean[]{true, true, false, false},
                new boolean[]{false, false, true, true});
        List<Placement> placements = List.of(dummyPlacement("A"), dummyPlacement("B"));

        ExactCoverSolver solver = new ExactCoverSolver(4, rows, placements);
        List<Integer> result = solver.solve();

        assertNotNull(result);
        assertEquals(2, result.size());
        Set<Integer> s = new HashSet<>(result);
        assertTrue(s.contains(0));
        assertTrue(s.contains(1));
    }

    /**
     * Conflicting rows: two rows each cover column 0. Only one can be chosen.
     * If the rest of the columns can't be covered, expect null.
     */
    @Test
    void handlesConflictingRows() {
        // 3 columns
        // Row 0: covers col 0
        // Row 1: covers col 0, col 1
        // Row 2: covers col 1, col 2
        // No exact cover for all 3 columns exists using these rows
        List<boolean[]> rows = List.of(
                new boolean[]{true, false, false},
                new boolean[]{true, true, false},
                new boolean[]{false, true, true});
        List<Placement> placements = List.of(
                dummyPlacement("A"), dummyPlacement("B"), dummyPlacement("C"));

        ExactCoverSolver solver = new ExactCoverSolver(3, rows, placements);
        List<Integer> result = solver.solve();

        // There IS a solution: row 0 (col 0) + row 2 (cols 1,2)
        assertNotNull(result);
        Set<Integer> s = new HashSet<>(result);
        assertTrue(s.contains(0) && s.contains(2) || s.contains(1),
                "Solution should be rows {0,2} or {1} with extra row");
    }
}

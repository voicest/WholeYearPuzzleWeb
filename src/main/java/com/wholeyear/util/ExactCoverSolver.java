package com.wholeyear.util;



import java.util.*;

/**
 * A Dancing Links (DLX) implementation for Exact Cover.
 *   • numCols    = total number of “fillable Board cells”
 *   • rowsMatrix = List<boolean[]> where rowsMatrix[i][j]==true iff placement i covers column j
 *   • placements = parallel List<Placement>, so that row i ↔ placements.get(i)
 *
 * Calling solve() returns a List<Integer> of row-indices (i.e. indices into placements)
 * that exactly cover every column once. If no solution, returns null.
 */
public class ExactCoverSolver {
    //////////////////////////////////////////
    // Internal DLX node and column classes //
    //////////////////////////////////////////

    // Base node in the DLX toroidal linked structure
    private static class DLXNode {
        DLXNode L, R, U, D;
        ColumnHeader C;
        int rowIndex; // index of the placement row this node belongs to

        DLXNode() { L = R = U = D = this; }
        void linkDown(DLXNode above) {
            this.D = above.D;
            this.D.U = this;
            this.U = above;
            above.D = this;
        }
        void linkRight(DLXNode left) {
            this.R = left.R;
            this.R.L = this;
            this.L = left;
            left.R = this;
        }
    }

    // Column header with size and name (we’ll store column index in “name”)
    private static class ColumnHeader extends DLXNode {
        int size = 0;
        final String name;
        ColumnHeader(String name) {
            super();
            this.name = name;
            this.C = this;
        }
        void cover() {
            // remove header from left/right
            this.R.L = this.L;
            this.L.R = this.R;
            // for each row node down, remove its row from other columns
            for (DLXNode row = this.D; row != this; row = row.D) {
                for (DLXNode right = row.R; right != row; right = right.R) {
                    right.D.U = right.U;
                    right.U.D = right.D;
                    right.C.size--;
                }
            }
        }
        void uncover() {
            for (DLXNode row = this.U; row != this; row = row.U) {
                for (DLXNode left = row.L; left != row; left = left.L) {
                    left.C.size++;
                    left.D.U = left;
                    left.U.D = left;
                }
            }
            this.R.L = this;
            this.L.R = this;
        }
    }

    ///////////////////////////
    // Fields and constructor //
    ///////////////////////////

    private final ColumnHeader header;            // root of column headers
    private final List<ColumnHeader> colHeaders;  // one per Board‐cell (numCols)
    private final List<DLXNode> rowNodes;         // one node per row (any node in that row)
    private final List<Placement> placements;     // parallel to rowsMatrix
    private final List<DLXNode> solutionStack = new ArrayList<>();

    /**
     * @param numCols     number of columns (|fillableCells|)
     * @param rowsMatrix  List of boolean[ numCols ] where rowMatrix[i][j]==true iff
     *                    placement i covers column j
     * @param placements  parallel List of Placement
     */
    public ExactCoverSolver(int numCols, List<boolean[]> rowsMatrix, List<Placement> placements) {
        this.header = new ColumnHeader("root");
        this.colHeaders = new ArrayList<>(numCols);
        this.rowNodes = new ArrayList<>(rowsMatrix.size());
        this.placements = placements;

        // 1) Create and link column headers
        for (int c = 0; c < numCols; c++) {
            ColumnHeader col = new ColumnHeader("C" + c);
            col.linkRight(header.L);
            colHeaders.add(col);
        }

        // 2) Build the sparse matrix of DLXNodes
        for (int r = 0; r < rowsMatrix.size(); r++) {
            boolean[] rowMask = rowsMatrix.get(r);
            DLXNode prev = null;
            DLXNode firstInRow = null;

            for (int c = 0; c < numCols; c++) {
                if (rowMask[c]) {
                    ColumnHeader col = colHeaders.get(c);
                    DLXNode node = new DLXNode();
                    node.C = col;
                    node.rowIndex = r;
                    // link into column
                    node.linkDown(col.U);
                    col.size++;
                    // link into this row
                    if (prev == null) {
                        firstInRow = node;
                        prev = node;
                    } else {
                        node.linkRight(prev);
                        prev = node;
                    }
                }
            }

            // close the row’s circular list (if any nodes exist)
            if (firstInRow != null) {
                // prev.R is already firstInRow thanks to linkRight above
                rowNodes.add(firstInRow);
            } else {
                // no columns covered by this row (should not happen in a valid placement)
                rowNodes.add(null);
            }
        }
    }

    //////////////////////////
    // Public solve method  //
    //////////////////////////

    /**
     * Runs DLX search. Returns a list of row‐indices (i.e. indices into placements)
     * that form an exact cover, or null if no solution.
     */
    public List<Integer> solve() {
        List<Integer> result = new ArrayList<>();
        if (search(0, result)) {
            return result;
        }
        return null;
    }

    //////////////////////
    // Algorithm X core //
    //////////////////////

    private boolean search(int k, List<Integer> result) {
        if (header.R == header) {
            // no columns left, we have covered everything
            // extract row‐indices from solutionStack
            for (DLXNode node : solutionStack) {
                result.add(node.rowIndex);
            }
            return true;
        }
        // choose column with minimal size (heuristic)
        ColumnHeader col = selectColumn();
        col.cover();

        for (DLXNode r = col.D; r != col; r = r.D) {
            solutionStack.add(r);
            for (DLXNode j = r.R; j != r; j = j.R) {
                j.C.cover();
            }
            if (search(k + 1, result)) {
                return true;
            }
            // backtrack
            solutionStack.remove(solutionStack.size() - 1);
            for (DLXNode j = r.L; j != r; j = j.L) {
                j.C.uncover();
            }
        }
        col.uncover();
        return false;
    }

    private ColumnHeader selectColumn() {
        int minSize = Integer.MAX_VALUE;
        ColumnHeader best = null;
        for (ColumnHeader c = (ColumnHeader) header.R; c != header; c = (ColumnHeader) c.R) {
            if (c.size < minSize) {
                minSize = c.size;
                best = c;
            }
        }
        return best;
    }
}

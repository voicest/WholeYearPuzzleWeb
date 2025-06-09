package com.wholeyear.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wholeyear.model.Board;
import com.wholeyear.model.Cell;
import com.wholeyear.model.Piece;
import com.wholeyear.util.Placement;
import com.wholeyear.util.ExactCoverSolver;

public class Solver {

    private final Board board;
    private final List<Piece> pieces;

    public Solver(Board board, List<Piece> pieces) {
        this.board = board;
        this.pieces = pieces;
    }
    
    public List<Placement> solve() {
        // ─── 5.1) Get all fillable cells (after holes) ───────────────────────
        List<Cell> fillableCells = board.getAllFillableCells();
        int numFillCols = fillableCells.size();
        Map<Cell,Integer> cellToIndex = new HashMap<>();
        for (int i = 0; i < fillableCells.size(); i++) {
            cellToIndex.put(fillableCells.get(i), i);
        }

        // ─── 5.2) Generate all placements ──────────────────────────────────
        List<Placement> allPlacements = generateAllPlacements();

        // ─── 5.3) Collect unique piece IDs and map them to “piece‐columns” ──
        // We know each Placement has a pieceId; gather them in insertion order:
        List<String> pieceIds = allPlacements.stream()
            .map(Placement::getPieceId)
            .distinct()
            .collect(Collectors.toList());
        int numPieces = pieceIds.size();
        // Map each pieceId to a column index in [numFillCols .. numFillCols + numPieces − 1]
        Map<String,Integer> pieceToCol = new HashMap<>();
        for (int i = 0; i < numPieces; i++) {
            pieceToCol.put(pieceIds.get(i), numFillCols + i);
        }

        // ─── 5.4) Build the exact-cover matrix ───────────────────────────────
        int totalCols = numFillCols + numPieces;
        List<boolean[]> rowsMatrix = new ArrayList<>(allPlacements.size());
        for (Placement plc : allPlacements) {
            boolean[] row = new boolean[totalCols];
            // 5.4.1) Mark the board‐cells it covers
            for (Cell c : plc.getCoveredCells()) {
                int idx = cellToIndex.get(c);
                row[idx] = true;
            }
            // 5.4.2) Mark the piece‐column (so this placement “uses” that piece)
            int pcol = pieceToCol.get(plc.getPieceId());
            row[pcol] = true;

            rowsMatrix.add(row);
        }

        // ─── 5.5) Run DLX with the enlarged matrix ──────────────────────────
        ExactCoverSolver solver = new ExactCoverSolver(totalCols, rowsMatrix, allPlacements);
        List<Integer> solutionRows = solver.solve();
        List<Placement> solutionPlacements = new ArrayList<>();

        if (solutionRows == null) {
            // No solution found
            System.err.println("No solution found available.");  
            return null; // or handle as needed
        } else {
            // Return the placement objects that form the solution
            
            for (int idx : solutionRows) {
                Placement plc = allPlacements.get(idx);
                solutionPlacements.add(plc);
                
                
            }
            return solutionPlacements; // Return the placements that form the solution
            
        }
    }

    public List<Placement> generateAllPlacements() {
            //List<Piece> pieces = loadAllPieces();
            List<Placement> placements = new ArrayList<>();

            for (Piece p : pieces) {
                List<List<Cell>> orients = p.generateAllOrientations();
                for (int o = 0; o < orients.size(); o++) {
                    List<Cell> shape = orients.get(o);
                    // bounding‐box of this orientation:
                    int maxR = shape.stream().mapToInt(Cell::getRow).max().orElse(0);
                    int maxC = shape.stream().mapToInt(Cell::getCol).max().orElse(0);

                    for (int r0 = 0; r0 + maxR < board.getRows(); r0++) {
                        for (int c0 = 0; c0 + maxC < board.getCols(); c0++) {
                            boolean ok = true;
                            List<Cell> absCells = new ArrayList<>();
                            for (Cell rel : shape) {
                                int rr = r0 + rel.getRow();
                                int cc = c0 + rel.getCol();
                                if (!board.isFillable(rr, cc)) {
                                    ok = false;
                                    break;
                                }
                                absCells.add(new Cell(rr, cc));
                            }
                            if (ok) {
                                placements.add(new Placement(p.getId(), o, r0, c0, absCells));
                            }
                        }
                    }
                }
            }
            return placements;
        
  
    }
}

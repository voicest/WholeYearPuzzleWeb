package com.wholeyear.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Encapsulates a single puzzle piece. Internally stores:
 *  - id: a name or label for the piece (e.g. "L1", "T2", etc.)
 *  - canonicalCells: list of (row,col) coords, normalized so min row=0, min col=0.
 *  - originalHeight/originalWidth: bounding‐box size of the canonical template.
 *
 * Once constructed, you can ask for all distinct orientations (rotations/flips)
 * as lists of relative Cells (each normalized so its min‐row/min‐col=0).
 */
public class Piece {
    private final String id;
    private final List<Cell> canonicalCells;
    private final int originalHeight, originalWidth;

    /**
     * @param id        A unique name for this piece
     * @param asciiTpl  A List<String> where each string is the same length.
     *                  '#' marks a cell belonging to the piece; anything else is empty.
     *
     * Example usage:
     *   List<String> lShape = Arrays.asList("#.", "#.", "##");
     *   Piece L = new Piece("L1", lShape);
     */
    public Piece(String id, List<String> asciiTpl) {
        this.id = id;

        // 1) Collect all '#' coordinates into a temporary list
        List<Cell> raw = new ArrayList<>();
        int tplRows = asciiTpl.size();
        int tplCols = asciiTpl.get(0).length();
        for (int r = 0; r < tplRows; r++) {
            String row = asciiTpl.get(r);
            if (row.length() != tplCols) {
                throw new IllegalArgumentException(
                  "All rows of asciiTpl must have equal length");
            }
            for (int c = 0; c < tplCols; c++) {
                if (row.charAt(c) == '#') {
                    raw.add(new Cell(r, c));
                }
            }
        }

        if (raw.isEmpty()) {
            throw new IllegalArgumentException("Piece " + id + " has no '#' cells.");
        }

        // 2) Normalize so minRow=0, minCol=0
        int minR = raw.stream().mapToInt(Cell::getRow).min().orElse(0);
        int minC = raw.stream().mapToInt(Cell::getCol).min().orElse(0);
        List<Cell> norm = raw.stream()
            .map(rc -> new Cell(rc.getRow() - minR, rc.getCol() - minC))
            .collect(Collectors.toList());

        this.canonicalCells = Collections.unmodifiableList(norm);

        // 3) Compute original bounding‐box (height = maxRow+1, width = maxCol+1)
        int maxR = norm.stream().mapToInt(Cell::getRow).max().orElse(0);
        int maxC = norm.stream().mapToInt(Cell::getCol).max().orElse(0);
        this.originalHeight = maxR + 1;
        this.originalWidth = maxC + 1;
    }

    public String getId() {
        return id;
    }

    public List<Cell> getCanonicalCells() {
        return canonicalCells;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    /**
     * Rotate a shape (list of Cells) 90° clockwise around (0,0)
     * given that the shape’s bounding‐box is (height × width).
     * NewRow = oldCol; NewCol = (height-1) - oldRow.
     */
    private static List<Cell> rotate90(List<Cell> shape, int height, int width) {
        List<Cell> out = new ArrayList<>();
        for (Cell rc : shape) {
            int r = rc.getRow(), c = rc.getCol();
            int nr = c;
            int nc = (height - 1 - r);
            out.add(new Cell(nr, nc));
        }
        return out;
    }

    /**
     * Generate all distinct orientations (rotations + optional horizontal flip) of this piece.
     * Each orientation is returned as a List<Cell> normalized so that its minRow/minCol = 0.
     */
    public List<List<Cell>> generateAllOrientations() {
        List<List<Cell>> variants = new ArrayList<>();
        Set<String> seenSignatures = new HashSet<>();

        // We’ll start from the canonicalCells (“no flip, no rotation”), then:
        // for flip=0 (no flip) and flip=1 (horizontal flip), apply 4 rotations each.
        for (int flip = 0; flip < 2; flip++) {
            List<Cell> flipped = new ArrayList<>();
            if (flip == 1) {
                // horizontal flip: newCol = (originalWidth - 1 - oldCol)
                for (Cell rc : canonicalCells) {
                    flipped.add(new Cell(rc.getRow(), originalWidth - 1 - rc.getCol()));
                }
            } else {
                // no flip: just copy
                flipped.addAll(canonicalCells);
            }

            // Now apply 4 rotations to `flipped`
            List<Cell> currentShape = flipped;
            int curH = originalHeight;
            int curW = originalWidth;
            for (int rot = 0; rot < 4; rot++) {
                // 1) Normalize currentShape so minRow=0, minCol=0
                int minR2 = currentShape.stream().mapToInt(Cell::getRow).min().orElse(0);
                int minC2 = currentShape.stream().mapToInt(Cell::getCol).min().orElse(0);
                List<Cell> norm = currentShape.stream()
                    .map(rc -> new Cell(rc.getRow() - minR2, rc.getCol() - minC2))
                    .collect(Collectors.toList());

                // 2) Build a “signature” (sorted coords as a single string) to detect duplicates
                List<Cell> sorted = new ArrayList<>(norm);
                sorted.sort(Comparator
                    .comparing(Cell::getRow)
                    .thenComparing(Cell::getCol));

                StringBuilder sig = new StringBuilder();
                for (Cell p : sorted) {
                    sig.append(p.getRow()).append(",").append(p.getCol()).append(";");
                }
                String signature = sig.toString();

                // 3) If unseen, add to variants
                if (!seenSignatures.contains(signature)) {
                    seenSignatures.add(signature);
                    variants.add(norm);
                }

                // 4) Rotate 90° for next iteration
                List<Cell> rotated = rotate90(currentShape, curH, curW);
                currentShape = rotated;
                // After rotation, bounding‐box dims swap: new height = curW, new width = curH
                int tmp = curH; 
                curH = curW; 
                curW = tmp;
            }
        }

        return variants;
    }
}

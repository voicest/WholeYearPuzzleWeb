package com.wholeyear.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wholeyear.model.Board;
import com.wholeyear.model.Cell;
import com.wholeyear.model.Piece;

public class Defintion {
    
      public static List<Piece> loadAllPieces() {
        List<Piece> pieces = new ArrayList<>();

        //Piece 1: “The small L”
        List<String> L_small = Arrays.asList(
                "#.",
                "#.",
                "##");
        pieces.add(new Piece("L_small", L_small));
        
        //Piece 2: “The big L”
        List<String> L_big = Arrays.asList(
                "#.",
                "#.",
                "#.",
                "##");
        pieces.add(new Piece("L_big", L_big));


        // Piece 3: “Square‐2x2”
        List<String> square2 = Arrays.asList(
                "##",
                "##");
        pieces.add(new Piece("S1", square2));

        // Piece 4: “T‐tetromino”
        List<String> T_tetra = Arrays.asList(
                ".#.",
                "###",
                "...");
        pieces.add(new Piece("T1", T_tetra));

        // Piece 5: “Lightning‐bolt”
        List<String> lightning = Arrays.asList(
            ".##",
            "##.");
        pieces.add(new Piece("Lightning", lightning));
    
        // Piece 6: “Bridge”
        List<String> bridge = Arrays.asList(
            "###",
            "#.#");
        pieces.add(new Piece("Bridge", bridge));

        // Piece 7: “Lightning‐bolt-big”
        List<String> lighteningbig = Arrays.asList(
            ".##",
            ".#.",
            "##.");
        pieces.add(new Piece("LightningBig", lighteningbig));

        // Piece 8: "Square plus”
        List<String> squarePlus = Arrays.asList(
            ".#",
            "##",
            "##");
        pieces.add(new Piece("SquarePlus", squarePlus));

        // Piece 9: "Cross”
        List<String> cross = Arrays.asList(
            ".#.",
            "###",
            ".#.");
        pieces.add(new Piece("Cross", cross));

        return pieces;
    }

    public static List<Piece> loadSimplePieces() {
        List<Piece> pieces = new ArrayList<>();

        // Example Piece 1: “Square”
        List<String> Square = Arrays.asList(
                "##",
                "##");
        pieces.add(new Piece("Square", Square));

         // Example Piece 2: “|”
        List<String> i = Arrays.asList(
                "#.");
                
        pieces.add(new Piece("|", i));

        /*         // Example Piece 2: “|”
        List<String> t = Arrays.asList(
                "#.");
        pieces.add(new Piece("t", t));
*/
        return pieces;
    }

      public static Board createBoard(List<String> asciiShape, List<List<String>> labels) {
        if (asciiShape == null || labels == null || asciiShape.size() != labels.size()) {
            throw new IllegalArgumentException("Invalid board shape or labels");
        }
        //int rows = asciiShape.size();
        int cols = asciiShape.get(0).length();
        for (String row : asciiShape) {
            if (row.length() != cols) {
                throw new IllegalArgumentException("All rows must have the same length");
            }
        }
        return new Board(asciiShape, labels);
    }

    // Create the specifc board for the Whole Year Puzzle.
    public static Board createWholeYearPuzzleBoard() {
        List<String> asciiShape = Arrays.asList(
                ".######..", // row 0: column 1-6 are fillable motnh row
                ".######..", // row 1: columns 1–6 fillable month rows
                ".#######.", // row 2: columns 1–7 fillable month date
                ".#######.", // row 3: columns 1–7 fillable month date
                ".#######.", // row 4: columns 1–7 fillable month date
                ".#######.", // row 5: columns 1–7 fillable month date
                "...###..." // row 6: columns 1–7 fillable month date
        );

        List<List<String>> labels = Arrays.asList(
                // row 0:
                Arrays.asList(null, "Jan", "Feb", "Mar", "Apr", "May", "Jun", null, null),
                // row 1:
                Arrays.asList(null, "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", null, null),
                // row 2:
                Arrays.asList(null, "1", "2", "3", "4", "5", "6", "7", null),
                // row 3:
                Arrays.asList(null, "8", "9", "10", "11", "12", "13", "14", null),
                // row 4:
                Arrays.asList(null, "15", "16", "17", "18", "19", "20", "21", null),
                // row 5:
                Arrays.asList(null, "22", "23", "24", "25", "26", "27", "28", null),
                // row 6:
                Arrays.asList(null, null, null, "29", "30", "31", null, null, null));

        try {
            return createBoard(asciiShape, labels);
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating Whole Year Puzzle Board: " + e.getMessage());
            return null; // or handle as needed
        }
    }

  

}

package com.wholeyear.model;

import java.util.List;
import com.wholeyear.model.Cell;

public class PieceDto {

    private Integer id;
    private String name;
    private List<Cell> shape; // each Offset has rowOffset, colOffset


    public PieceDto(Integer id, String name, List<Cell> shape) {
        this.id = id;
        this.name = name;
        this.shape = shape;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Cell> getShape() {
        return shape;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShape(List<Cell> shape) {
        this.shape = shape;
    }
}
package com.example.cody.slidingtiles;

/**
 * Created by cody on 3/12/18.
 */

public class Piece {
    public int row;
    public int col;
    public int emptyRow;
    public int emptyCol;

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public void setCol(int col) {
        this.col = col;
    }
    public int getEmptyRow() {return emptyRow;}
    public int getEmptyCol() {return emptyCol;}
    public void setEmptyRow(int emptyRow) {this.emptyRow = emptyRow;}
    public void setEmptyCol(int emptyCol) {this.emptyCol = emptyCol;}
}

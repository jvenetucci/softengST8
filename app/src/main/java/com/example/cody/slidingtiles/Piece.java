package com.example.cody.slidingtiles;

/**
 * Created by cody on 3/12/18.
 */

//class holds location of move and empty space
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

    public Piece() {
        row = -1;
        col = -1;
        emptyRow = -1;
        emptyCol = -1;
    }

    public void copy(Piece toCopy) {
        this.row = toCopy.row;
        this.col = toCopy.col;
        this.emptyRow = toCopy.emptyRow;
        this.emptyCol = toCopy.emptyCol;
    }
}

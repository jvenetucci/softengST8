package com.example.cody.slidingtiles;

import java.util.Arrays;
import java.util.ArrayList;
import java.lang.Math;
import java.lang.Object;

public class AiState implements Comparable<AiState> {
    private AiState parent;
    public int cost;
    public int depth;
    public int eval;
    public int[][] board = new int[5][5];
    public int blanki;
    public int blankj;
    public int numActions;

    //Constructor
    public AiState() {
        for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 5; ++j) {
                this.board[i][j] = 0;
            }
        }
        this.parent = null;
        this.cost = 0;
        this.depth = 0;
        this.eval = 0;
        this.blanki = -1;
        this.blankj = -1;
        this.numActions = 0;
    }

    //Returns parent reference
    public AiState getParent() {
        return parent;
    }

    //Sets parent reference
    public void setParent(AiState parent) {
        this.parent = parent;
    }

    //Returns a negative integer, zero, or a positive integer if this
    //object is less than, equal to, or greater than the object passed in
    @Override public int compareTo(AiState compare) {
        int diff = 0;

        if(this == compare)
            return 0;

        if(this.eval >= compare.eval)
            diff = 1;
        else if(this.eval < compare.eval)
            diff = -1;

        return diff;
    }

    //
    @Override public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof AiState)) {
            return false;
        }

        boolean match = true;
        AiState toCheck = (AiState)obj;

        for(int i = 0; i < 5 && match; ++i) {
            for(int j = 0; j < 5 && match; ++j) {
                if(this.board[i][j] != toCheck.board[i][j]) {
                    match = false;
                }
            }
        }

        if(match) {
            if(this.parent == toCheck.parent && this.cost == toCheck.cost &&
                    this.depth == toCheck.depth && this.eval == toCheck.eval &&
                    this.blanki == toCheck.blanki && this.blankj == toCheck.blankj &&
                    this.numActions == toCheck.numActions) {
                match = true;
            }
            else {
                match = false;
            }
        }
        return match;
    }

    //
    @Override public int hashCode() {
        int result = 1;
        int prime = 31;

        result = prime * result + cost;
        result = prime * result + depth;
        result = prime * result + eval;
        result = prime * result + blanki;
        result = prime * result + blankj;
        result = prime * result + numActions;

        return result;
    }

    //
    public void copy(int [][] toCopy) {
        for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 5; ++j) {
                if(toCopy[i][j] == -1) {
                    this.blanki = i;
                    this.blankj = j;
                }
                this.board[i][j] = toCopy[i][j];
            }
        }
    }

    //
    public void copy(AiState toCopy) {
        this.copy(toCopy.board);
        this.parent = toCopy.getParent();
        this.cost = toCopy.cost;
        this.depth = toCopy.depth;
        this.eval = toCopy.eval;
        this.numActions = toCopy.numActions;
    }

    //
    public Piece [] determineActions() {
        Piece[] move = new Piece[4];
        for(int i = 0; i < move.length; ++i) {
            move[i] = new Piece();
        }
        if(blanki < 0 || blanki > 4) {
            System.out.println("Invalid blank row: " + blanki);
        }
        if(blankj < 0 || blankj > 4) {
            System.out.println("invalid blank column: " + blankj);
        }

        if(blanki == 0 || blanki == 4) { //corner or side

            if(blankj == 0 || blankj == 4) { //corner

                numActions = 2;

                if(blanki == 0) { //can't move up
                    if(blankj == 0) { //can't move left
                        move[0].setRow(blanki);
                        move[0].setCol(blankj+1);
                        move[1].setRow(blanki+1);
                        move[1].setCol(blankj);
                    }
                    else { //can't move right
                        move[0].setRow(blanki);
                        move[0].setCol(blankj-1);
                        move[1].setRow(blanki+1);
                        move[1].setCol(blankj);
                    }
                }
                else { //can't move down
                    if(blankj == 0) { //can't move left
                        move[0].setRow(blanki);
                        move[0].setCol(blankj+1);
                        move[1].setRow(blanki-1);
                        move[1].setCol(blankj);
                    }
                    else { //can't move right

                        move[0].setRow(blanki);
                        move[0].setCol(blankj-1);
                        move[1].setRow(blanki-1);
                        move[1].setCol(blankj);
                    }
                }
            }
            else { //side
                numActions = 3;

                if(blanki == 0) { //can't move up
                    move[0].setRow(blanki);
                    move[0].setCol(blankj-1);
                    move[1].setRow(blanki+1);
                    move[1].setCol(blankj);
                    move[2].setRow(blanki);
                    move[2].setCol(blankj+1);

                }
                else { //can't move down
                    move[0].setRow(blanki);
                    move[0].setCol(blankj-1);
                    move[1].setRow(blanki);
                    move[1].setCol(blankj+1);
                    move[2].setRow(blanki-1);
                    move[2].setCol(blankj);
                }
            }
        }
        else { //middle or side
            if (blankj == 0 || blankj == 4) { //side
                numActions = 3;

                if (blankj == 0) { //can't move left
                    move[0].setRow(blanki + 1);
                    move[0].setCol(blankj);
                    move[1].setRow(blanki);
                    move[1].setCol(blankj + 1);
                    move[2].setRow(blanki - 1);
                    move[2].setCol(blankj);
                }
                else { //can't move right
                    move[0].setRow(blanki);
                    move[0].setCol(blankj - 1);
                    move[1].setRow(blanki + 1);
                    move[1].setCol(blankj);
                    move[2].setRow(blanki - 1);
                    move[2].setCol(blankj);
                }
            }
            else { //middle
                numActions = 4;

                move[0].setRow(blanki);
                move[0].setCol(blankj - 1);
                move[1].setRow(blanki + 1);
                move[1].setCol(blankj);
                move[2].setRow(blanki);
                move[2].setCol(blankj + 1);
                move[3].setRow(blanki - 1);
                move[3].setCol(blankj);
            }
        }

        for(int i = 0; i < numActions; ++i) {
            move[i].setEmptyRow(blanki);
            move[i].setEmptyCol(blankj);
        }

        return move;
    }

    //
    public int makeMove(Piece action) {
        int newRow = action.getRow();
        int newCol = action.getCol();
        int temp = board[newRow][newCol];

        board[newRow][newCol] = board[blanki][blankj];
        board[blanki][blankj] = temp;

        //set new blank row for child
        blanki = newRow;
        blankj = newCol;

        //capture depth for A*
        depth = depth + 1;

        evaluate();
        return 0;
    }

    //
    public void evaluate() {
        int returnVal = 0;
        int hold = 0;
        int horzConflict = 0;
        int vertConflict = 0;
        int maxi = 0;

        //XXX Find way to optimize
        for(int i = 0; i < 5; ++i){
            maxi = -1;
            for(int j = 0; j < 5; ++j){
                hold = board[i][j];
                //check for manhattan distance for i range
                if(hold >= 1 && hold <= 5){
                    returnVal += Math.abs(0 - i);
                    if(i == 0){
                        if(hold > maxi)
                            maxi = hold;
                        else
                            ++horzConflict;
                    }
                }
                else if(hold >= 6 && hold <= 10){
                    returnVal += Math.abs(1 - i);
                    if(i == 1){
                        if(hold > maxi)
                            maxi = hold;
                        else
                            ++horzConflict;
                    }
                }
                else if(hold >= 11 && hold <= 15){
                    returnVal += Math.abs(2 - i);
                    if(i == 2){
                        if(hold > maxi)
                            maxi = hold;
                        else
                            ++horzConflict;
                    }

                }
                else if(hold >= 16 && hold <= 20){
                    returnVal += Math.abs(3 - i);
                    if(i == 3){
                        if(hold > maxi)
                            maxi = hold;
                        else
                            ++horzConflict;
                    }
                }
                else if(hold >= 21 && hold <= 24){
                    returnVal += Math.abs(4 - i);
                    if(i == 4){
                        if(hold > maxi)
                            maxi = hold;
                        else
                            ++horzConflict;
                    }
                }

                //check for manhattan distance for j range
                if(hold == 1 || hold == 6 || hold == 11 || hold == 16 || hold == 21) {
                    returnVal += Math.abs(0 - j);
                }
                else if(hold == 2 || hold == 7 || hold == 12 || hold == 17 || hold == 22) {
                    returnVal += Math.abs(1 - j);
                }
                else if(hold == 3 || hold == 8 || hold == 13 || hold == 18 || hold == 23) {
                    returnVal += Math.abs(2 - j);
                }
                else if(hold == 4 || hold == 9 || hold == 14 || hold == 19 || hold == 24) {
                    returnVal += Math.abs(3 - j);
                }
                else if(hold == 5 || hold == 10 || hold == 15 || hold == 20){
                    returnVal += Math.abs(4 - j);
                }
            }
        }

        int max;
        //calc vert linear conflicts
        for(int j = 0; j < 5; ++j){
            max = -1;
            for(int i = 0; i < 5; ++i){
                hold = this.board[i][j];

                if(hold == 1 || hold == 6 || hold == 11 || hold == 16 || hold == 21) {
                    if(j == 0){
                        if(hold > max)
                            max = hold;
                        else
                            ++vertConflict;
                    }

                }
                else if(hold == 2 || hold == 7 || hold == 12 || hold == 17 || hold == 22) {
                    if(j == 1){
                        if(hold > max)
                            max = hold;
                        else
                            ++vertConflict;
                    }
                }
                else if(hold == 3 || hold == 8 || hold == 13 || hold == 18 || hold == 23) {
                    if(j == 2){
                        if(hold > max)
                            max = hold;
                        else
                            ++vertConflict;
                    }
                }
                else if(hold == 4 || hold == 9 || hold == 14 || hold == 19 || hold == 24) {
                    if(j == 3){
                        if(hold > max)
                            max = hold;
                        else
                            ++vertConflict;
                    }
                }
                else if(hold == 5 || hold == 10 || hold == 15 || hold == 20){
                    if(j == 4){
                        if(hold > max)
                            max = hold;
                        else
                            ++vertConflict;
                    }
                }
            }
        }

        //        System.out.println("Horizontal Conflict: " + horzConflict);
        //        System.out.println("Vertical Conflict: " + vertConflict);

        returnVal += 2 * (horzConflict + vertConflict);

        cost = returnVal;
        //Add depth to eval to implement A*
        returnVal += depth;
        eval = returnVal;

    }

    //
    public int [][] solution() {
        AiState grab = this;
        AiState current = this;

        while(grab != null && grab.getParent() != null) {
            current = grab;
            grab = grab.getParent();
        }

        return current.board;
    }


}

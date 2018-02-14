package com.example.cody.slidingtiles;

import java.util.Random;

/**
 * A helper class for generating 2d matrix representations of sliding tile boards
 * Contains methods to generate 5x5 matrices of Number and Math Mode
 */

class BoardGenerator {

    // Returns a 5x5 matrix that represents a Math Mode Board
    // The first four rows from the top contain valid equations
    // The last row is just numbers
    int [][] generateMathModeBoard() {
        int [][] tileMatrix = new int[5][5];
        Random tileGenerator = new Random();
        tileGenerator.setSeed(System.currentTimeMillis());
        int numberBound = 0;
        int curNum;
        int divisionCase;
        int divisionFlag;

        for (int i =0; i <5; i++) {
            divisionFlag = 0;
            if( i == 4){
                for (int k = 0; k < 4; k++){
                    tileMatrix[i][k] = tileGenerator.nextInt(10);
                }
            }
            else{
                tileMatrix[i][0] = tileGenerator.nextInt(10);
                curNum = tileMatrix[i][0];
                tileMatrix[i][1] = 11 + tileGenerator.nextInt(4);

                switch (tileMatrix[i][1]) {
                    case 11: // add
                        numberBound = 10 - curNum;
                        break;
                    case 12: // subtract
                        while(tileMatrix[i][0] == 0){
                            tileMatrix[i][0] = tileGenerator.nextInt(10);
                        }
                        curNum = tileMatrix[i][0];
                        numberBound = curNum + 1;
                        break;
                    case 13: // multiply
                        if (curNum > 4) {
                            numberBound = 1;
                        } else if (curNum == 4) {
                            numberBound = 2;
                        } else if (curNum == 3) {
                            numberBound = 3;
                        } else if (curNum == 2) {
                            numberBound = 4;
                        } else if (curNum < 2) {
                            numberBound = 10;
                        }
                        break;
                    case 14: //divide
                        divisionFlag = 1;
                        if (curNum == 0) {
                            while(tileMatrix[i][0] == 0){
                                tileMatrix[i][0] = tileGenerator.nextInt(10);
                            }
                            curNum = tileMatrix[i][0];
                        }
                        if (curNum == 9) {
                            divisionCase = tileGenerator.nextInt(3);
                            if (divisionCase == 0) {
                                tileMatrix[i][2] = 1;
                            } else if (divisionCase == 1) {
                                tileMatrix[i][2] = 3;
                            } else if (divisionCase == 2) {
                                tileMatrix[i][2] = 9;
                            }
                        } else if (curNum % 2 == 0) {
                            divisionCase = tileGenerator.nextInt(4);
                            if (divisionCase == 0) {
                                tileMatrix[i][2] = 1;
                            } else if (divisionCase == 1) {
                                tileMatrix[i][2] = 2;
                            } else if (divisionCase == 2) {
                                tileMatrix[i][2] = curNum;
                            } else if (divisionCase == 3) {
                                tileMatrix[i][2] = curNum / 2;
                            }
                        } else {
                            divisionCase = tileGenerator.nextInt(2);
                            if (divisionCase == 0) {
                                tileMatrix[i][2] = 1;
                            } else if (divisionCase == 1) {
                                tileMatrix[i][2] = curNum;
                            }
                        }
                        break;
                    default:
                        break;
                }
                if (divisionFlag == 0) {
                    tileMatrix[i][2] = tileGenerator.nextInt(numberBound);
                }
                tileMatrix[i][3] = 10;
                switch (tileMatrix[i][1]) {
                    case 11:
                        tileMatrix[i][4] = tileMatrix[i][0] + tileMatrix[i][2];
                        break;
                    case 12:
                        tileMatrix[i][4] = tileMatrix[i][0] - tileMatrix[i][2];
                        break;
                    case 13:
                        tileMatrix[i][4] = tileMatrix[i][0] * tileMatrix[i][2];
                        break;
                    case 14:
                        //tileMatrix[i][4] = 99;
                        tileMatrix[i][4] = tileMatrix[i][0] / tileMatrix[i][2];
                        break;
                    default:
                        break;
                }

            }

        }
        return tileMatrix;
    }

    // Returns a 5x5 matrix that represents a Number Mode Board
    // The board is in solved order, with 1 in the top left and the blank tile in the bottom right.
    int [][] generateNumberModeBoard() {
        int [][] tileMatrix = new int [5][5];
        int count = 1;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                tileMatrix[i][j] = count;
                count++;
            }
        }
        tileMatrix[4][4] = -1; //Set bottom left to empty tile
        return tileMatrix;
    }

    void shuffleBoard(int tileMatrix[][]){
        Random shuffler = new Random();
        shuffler.setSeed(System.currentTimeMillis());
        int targetRow, targetCol, sourceRow, sourceCol, temp;
        for (int i = 0; i <100; i++){
            do {
                targetRow = shuffler.nextInt(5);
                targetCol = shuffler.nextInt(5);
            } while( targetRow == 4 && targetCol ==4);
            do {
                sourceRow = shuffler.nextInt(5);
                sourceCol = shuffler.nextInt(5);
            } while( sourceRow == 4 && sourceCol == 4);

            temp = tileMatrix[sourceRow][sourceCol];
            tileMatrix[sourceRow][sourceCol] = tileMatrix[targetRow][targetCol];
            tileMatrix[targetRow][targetCol] = temp;
        }
    }
}

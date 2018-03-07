package com.example.cody.slidingtiles;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

/**
 * Unit tests for verifying valid board generation.
 */
public class BoardGeneratorTest {
    private BoardGenerator boardGen = new BoardGenerator();
    private int [][] board;

    @Test
    public void mathModeBoardGenerates10inCol3() {
        board = boardGen.generateMathModeBoard();

        for (int i = 0; i < 4; i++) {
            assertEquals(board[i][3], 10);
        }
    }

    @Test
    public void mathModeGenerates0Through9InCol0and2and4() {
        board = boardGen.generateMathModeBoard();

        for (int i = 0; i < 4; i++) {
            assertTrue((board[i][0] >= 0) && (board[i][0] <= 9));
            assertTrue((board[i][2] >= 0) && (board[i][2] <= 9));
            assertTrue((board[i][4] >= 0) && (board[i][4] <= 9));
        }
    }

    @Test
    public void mathModeGenerates0Through9InRow4() {
        board = boardGen.generateMathModeBoard();

        for (int i =0; i < 5; i++) {
            assertTrue((board[4][i] >= 0) && (board[4][i] <= 9));
        }
    }

    @Test
    public void numberModeGeneratesNumbers1Through24InSequence() {
        board = boardGen.generateNumberModeBoard();
        int currentCount = 1;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (i == 4 && j == 4) {
                    assertTrue(board[i][j] == -1);
                } else {
                    assertTrue(board[i][j] == currentCount);
                }
                currentCount++;
            }
        }
        assertEquals(26, currentCount);
    }

}
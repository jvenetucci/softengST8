package com.example.cody.slidingtiles;

import android.graphics.Color;
import android.graphics.Rect;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.Random;

public class MathMode extends AppCompatActivity {

    int tileMatrix [] [] = new int[5][5];
    Button emptyTileButton;
    float xTileDistance = 0;
    float yTileDistance = 0;
    private Button btnShuffle;
    GridLayout board;
    ViewGroup submissionHistoryWindow;
    MathSolutionHandler equationHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_mode);

        //Create a 2-D array of the board
        generateBoardMatrix(tileMatrix);
        tileMatrix[4][4] = -1;  //Set this as the blank tile
    //    shuffleBoard(tileMatrix);
        //Move the contents of the 2-D array to the UI
        board = findViewById(R.id.board);
        displayBoardMatrixUI(board);

        btnShuffle = (Button) findViewById(R.id.btnShuffle);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            DemandShuffle(tileMatrix);
            }
        });
        //Initialize the solution handler
        equationHandler = new MathSolutionHandler();

        //Find the submission history window
        submissionHistoryWindow = findViewById(R.id.submissionHistory);
    }

    // Function that determines how far apart tile are.
    // The distance is dependent on screen size.
    // This should be called in the moveTile() method.
    private void obtainTileDistance() {
        View xButton = findViewById(R.id.xButton);
        View yButton = findViewById(R.id.yButton);

        xTileDistance = Math.abs(xButton.getX() - emptyTileButton.getX());
        yTileDistance = Math.abs(yButton.getY() - emptyTileButton.getY());
    }

    // General onTouchEvent used to submit player solutions
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int xPos = (int) event.getX();
        int yPos = (int) event.getY();

        int action = event.getAction();

        // While the user's finger is on the screen, lets record their submission.
        // When the user lifts up their finger, that signals the end of their submission
        if (action == MotionEvent.ACTION_UP) {
            TextView submission = new TextView(this);
            int score = equationHandler.solve();
            if (score < 0) {
                submission.setTextColor(Color.RED);
            } else {
                submission.setTextColor(Color.GREEN);
            }
            submission.setTextSize(20);
            submission.setBackgroundColor(Color.GRAY);
            submission.setText(equationHandler.getEquationString());
            submissionHistoryWindow.addView(submission);
            equationHandler.resetHandler();
            return true;
        } else {
            Button tile = (Button) findViewAt(board, xPos, yPos);
            if (tile != null) {
                equationHandler.addTile(tile);
            }
            return true;
        }
    }

    // Finds view within a Gridlayout
    // x & y are coordinates relative to layout.
    // Code Courtesy of Luke, with slight modifications by Joseph Venetucci
    // https://stackoverflow.com/a/36037991
    private View findViewAt(GridLayout viewGroup, int x, int y) {
        for(int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof GridLayout) {
                View foundView = findViewAt((GridLayout) child, x, y);
                if (foundView != null && foundView.isShown()) {
                    return foundView;
                }
            } else {
                int[] location = new int[2];
                child.getLocationOnScreen(location);
                Rect rect = new Rect(location[0], location[1], location[0] + child.getWidth(), location[1] + child.getHeight());
                if (rect.contains(x, y)) {
                    return child;
                }
            }
        }
        return null;
    }

    // Switches a tiles position with the empty tile
    // A valid move is if the tile to be moved and the empty tile:
    // 1) Differ by xTileDistance|yTileDistance units in either the x or y plane,
    // 2) Have the same value in the remaining plane.
    public void moveTile(View tile) {
        // If this is the first time we are calling this, lets get the tile distances
        if (xTileDistance == 0) {
            obtainTileDistance();
        }

        float currentX = tile.getX();
        float currentY = tile.getY();

        Button emptyTile = findViewById(R.id.emptyButton);
        float emptyY = emptyTile.getY();
        float emptyX = emptyTile.getX();

        if (((Math.abs(currentX - emptyX) == xTileDistance) && (currentY == emptyY)) || ((Math.abs(currentY - emptyY) == yTileDistance) && (currentX == emptyX))) {
            //Code that moves the TextViews
            tile.animate().x(emptyX).y(emptyY);
            emptyTile.animate().x(currentX).y(currentY);
        }
    }

    // Takes a 2-d array and maps it to UI elements
    protected void displayBoardMatrixUI(GridLayout board) {
        Button tile;
        int tileCount = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                tile = (Button) board.getChildAt(tileCount);
                switch(tileMatrix[i][j]) {
                    case -1:
                        tile.setText(" ");
                        emptyTileButton = tile;
                        break;
                    case 10:
                        tile.setText("=");
                        break;
                    case 11:
                        tile.setText("+");
                        break;
                    case 12:
                        tile.setText("-");
                        break;
                    case 13:
                        tile.setText("*");
                        break;
                    case 14:
                        tile.setText("/");
                        break;
                    default:
                        tile.setText(Integer.toString(tileMatrix[i][j]));
                        break;
                }
                tileCount ++;
            }
        }
    }

    // Generates a sliding tile puzzle in the form of a 2-d array
    protected void generateBoardMatrix(int tileMatrix[][]) {
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
            }

    private void shuffleBoard(int tileMatrix[][]){
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

    public void DemandShuffle(int tileMatrix[][]){
            shuffleBoard(tileMatrix);
            GridLayout board = findViewById(R.id.board);
            displayBoardMatrixUI(board);

        }
    }



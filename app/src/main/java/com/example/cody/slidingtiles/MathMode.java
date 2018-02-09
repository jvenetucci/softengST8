package com.example.cody.slidingtiles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.Random;

public class MathMode extends AppCompatActivity {

    int tileMatrix [] [] = new int[5][5];
    Button emptyTileButton;
    float xTileDistance = 0;
    float yTileDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_mode);

        //Create a 2-D array of the board
        generateBoardMatrix(tileMatrix);
        tileMatrix[4][4] = -1;  //Set this as the blank tile

        //Move the contents of the 2-D array to the UI
        GridLayout board = findViewById(R.id.board);
        displayBoardMatrixUI(board);



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

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (j % 2 == 0) {   //Values 0, 2, 4
                    tileMatrix [i] [j] = tileGenerator.nextInt(10);
                }
                else if (j == 3) {
                    tileMatrix [i] [j] = 10; // 10 == '='
                }
                else {
                    tileMatrix [i] [j] = 11 + tileGenerator.nextInt(4);
                }
            }
        }
    }
}



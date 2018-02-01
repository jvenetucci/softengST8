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

    // Switches a tiles position with the empty tile
    // A valid move is if the tile to be moved and the empty tile:
    // 1) Differ by |180| units in either the x or y plane,
    // 2) Have the same value in the remaining plane.
    protected void moveTile(View tile) {
        float currentX = tile.getX();
        float currentY = tile.getY();

        Button emptyTile = findViewById(R.id.emptyButton);
        float emptyY = emptyTile.getY();
        float emptyX = emptyTile.getX();

        if (((Math.abs(currentX - emptyX) == 150) && (currentY == emptyY)) || ((Math.abs(currentY - emptyY) == 144) && (currentX == emptyX))) {
            // Test code to display values. To be removed for final presentation.
//            TextView text = (TextView) findViewById(R.id.CurrentX);
//            text.setText(Float.toString(currentX));
//            text = (TextView) findViewById(R.id.CurrentY);
//            text.setText(Float.toString(currentY));
//            text = (TextView) findViewById(R.id.EmptyX);
//            text.setText(Float.toString(emptyX));
//            text = (TextView) findViewById(R.id.EmptyY);
//            text.setText(Float.toString(emptyY));

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



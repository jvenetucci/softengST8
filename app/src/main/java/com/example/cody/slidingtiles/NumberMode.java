package com.example.cody.slidingtiles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

public class NumberMode extends AppCompatActivity {

    //Board Resources
    int tileMatrix[][] = new int [5][5];
    float xTileDistance = 0;
    float yTileDistance = 0;
    int emptyTileRowIndex;
    int emptyTileColIndex;

    //UI Elements
    Button emptyTileButton;
    GridLayout board;

    //Helper Classes
    BoardGenerator boardGen = new BoardGenerator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_mode);

        //Create a 2-D array of the board
        tileMatrix = boardGen.generateNumberModeBoard();
        //Scramble Board

        //Move the contents of the 2-D array to the UI
        board = findViewById(R.id.board);
        displayBoardMatrixUI(board);

        //Find and map the empty tile
        emptyTileButton = findViewById(R.id.emptyButton);


    }

    // Takes a 2-d array and maps it to UI elements
    protected void displayBoardMatrixUI(final GridLayout board) {
        Button tile;
        int tileCount = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                tile = (Button) board.getChildAt(tileCount);
                if (tileMatrix[i][j] == -1) { // -1 is the empty tile
                    tile.setText(" ");
                    emptyTileRowIndex = i;
                    emptyTileColIndex = j;
                } else {
                    tile.setText(Integer.toString(tileMatrix[i][j]));
                }
                tile.setOnClickListener(new View.OnClickListener() { //Tie the moveTile method to onclick
                    @Override
                    public void onClick(View v) {
                        moveTile(v);
                        if (isSolved(tileMatrix)) {
                            Toast.makeText(v.getContext(), "YOU WIN!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                tileCount ++;
            }
        }
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

        // Move the tiles and update the 2d matrix
        if ((Math.abs(currentX - emptyX) == xTileDistance) && (currentY == emptyY)) { //Horizontal Move
            if (currentX > emptyX) { // Right
                swap(tileMatrix, emptyTileRowIndex, emptyTileColIndex + 1);
            } else { // Left
                swap(tileMatrix, emptyTileRowIndex, emptyTileColIndex - 1);
            }
            //Code that moves the TextViews
            tile.animate().x(emptyX).y(emptyY);
            emptyTile.animate().x(currentX).y(currentY);
        } else if ((Math.abs(currentY - emptyY) == yTileDistance) && (currentX == emptyX)) { //Vertical Move
            if (currentY < emptyY) { // Above
                swap(tileMatrix, emptyTileRowIndex - 1, emptyTileColIndex);
            } else { // Below
                swap(tileMatrix, emptyTileRowIndex + 1, emptyTileColIndex);
            }
            //Code that moves the TextViews
            tile.animate().x(emptyX).y(emptyY);
            emptyTile.animate().x(currentX).y(currentY);
        }
    }

    // Swap the tile located at matrix[row][col] with the 'empty tile'
    public void swap(int [][] matrix, int row, int col) {
        int temp = matrix[row][col];
        matrix[row][col] = matrix [emptyTileRowIndex][emptyTileColIndex];
        matrix[emptyTileRowIndex][emptyTileColIndex] = temp;
        // Update the position of the empty tile
        emptyTileRowIndex = row;
        emptyTileColIndex = col;
    }

    // Checks if the 2d matrix representation of the board is solved.
    // Goes index by index and checks that each one is correct
    protected boolean isSolved(int [][] boardMatrix) {
        int currentCount = 1;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (i == 4 && j ==4) { // If we made it to the bottom right corner, its solved!
                    return true;
                }
                if (boardMatrix[i][j] != currentCount) {
                    return false;
                }
                currentCount++;
            }
        }
        return true; //This should never execute.
    }

    //TBD
//    private void scramble() {
//        int index;
//        String temp;
//        Random random = new Random();
//
//        for (int i = tileList.length - 1; i > 0; i--) {
//            index = random.nextInt(i + 1);
//            temp = tileList[index];
//            tileList[index] = tileList[i];
//            tileList[i] = temp;
//        }
//    }
}

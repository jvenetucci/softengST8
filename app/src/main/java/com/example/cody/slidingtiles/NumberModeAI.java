package com.example.cody.slidingtiles;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class NumberModeAI extends AppCompatActivity {

    //Board Resources
    int tileMatrix[][] = new int [5][5];
    int AITileMatrix[][] = new int[5][5];
    float xTileDistance = 0;
    float yTileDistance = 0;
    int emptyTileRowIndex;
    int emptyTileColIndex;
    private HashMap<Integer, Button> AIBoardMap = new HashMap<>();


    // Timer variables
    //private Button startButton;
    private Button pauseButton;
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    //Popup window
    private Context mContext;
    private PopupWindow mPopupWindow;
    private ConstraintLayout mRelativeLayout,back_dim_layout;


    //UI Elements
    Button emptyTileButton;
    GridLayout board;
    GridLayout AIboard;

    //Helper Classes
    BoardGenerator boardGen = new BoardGenerator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_mode_ai);

        mContext = getApplicationContext();
        mRelativeLayout = (ConstraintLayout) findViewById(R.id.rl);
        // Timer implementation
        timerValue = (TextView) findViewById(R.id.timerValue);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        /* startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        }); */
        pauseButton = (Button) findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);


                //popup
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.popup,null);
                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
//
               mPopupWindow.setTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setOutsideTouchable(false);
                Button resumeButton = (Button) customView.findViewById(R.id.resume);
                Button closeButton = (Button) customView.findViewById(R.id.exit);
                Button highscoreButton = (Button) customView.findViewById(R.id.highscore);

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        System.exit(0);
                    }
                });
                resumeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);
                        mPopupWindow.dismiss();
                    }
                });
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);


            }
        });

        //Create a 2-D array of the board
        tileMatrix = boardGen.generateNumberModeBoard();
        copyMatrix(tileMatrix, AITileMatrix);

        //Move the contents of the 2-D array to the UI
        board = findViewById(R.id.board);
        displayBoardMatrixUI(board);
        AIboard = findViewById(R.id.AIboard);
        displayAIBoardMatrixUI(AIboard);
    }

    // Timer code
    public Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            customHandler.postDelayed(this, 0);
        }
    };

    // Takes a 2-d array and maps it to UI elements
    protected void displayBoardMatrixUI(final GridLayout board) {
        Button tile;
        int tileCount = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                tile = (Button) board.getChildAt(tileCount);
                if (tileMatrix[i][j] == -1) { // -1 is the empty tile
                    tile.setText(" ");
                    emptyTileButton = tile;
                    emptyTileRowIndex = i;  // Used in maintaining state of 2d Array
                    emptyTileColIndex = j;  // Used in maintaining state of 2d Array
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

    // Takes a 2-d array and maps it to UI elements
    // Used for the AI board. While the Array is being mapped to the UI,
    // The UI elements are being mapped in AIBoardMap
    protected void displayAIBoardMatrixUI(final GridLayout board) {
        Button tile;
        int number;
        int tileCount = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                tile = (Button) board.getChildAt(tileCount);
                number = AITileMatrix[i][j];
                if (number == -1) { // -1 is the empty tile
                    tile.setText(" ");
                } else {
                    tile.setText(Integer.toString(number));
                }
                AIBoardMap.put(number, tile);
                tileCount ++;
            }
        }
    }

    // Function that determines how far apart tile are.
    // The distance is dependent on screen size.
    // This should be called in the moveTile() method.
    // Calculates distances using tiles located in the lower right corner of the board.
    private void obtainTileDistance() {
        View xButton = findViewById(R.id.xButton);
        View yButton = findViewById(R.id.yButton);
        View lowerRightButton = findViewById(R.id.lowerRightButton);

        xTileDistance = Math.abs(xButton.getX() - lowerRightButton.getX());
        yTileDistance = Math.abs(yButton.getY() - lowerRightButton.getY());
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

        float emptyY = emptyTileButton.getY();
        float emptyX = emptyTileButton.getX();

        // Move the tiles and update the 2d matrix
        if ((Math.abs(currentX - emptyX) == xTileDistance) && (currentY == emptyY)) { //Horizontal Move
            if (currentX > emptyX) { // Right
                swapEmptyTile(tileMatrix, emptyTileRowIndex, emptyTileColIndex + 1);
            } else { // Left
                swapEmptyTile(tileMatrix, emptyTileRowIndex, emptyTileColIndex - 1);
            }
            //Code that moves the TextViews
            tile.animate().x(emptyX).y(emptyY);
            emptyTileButton.animate().x(currentX).y(currentY);
        } else if ((Math.abs(currentY - emptyY) == yTileDistance) && (currentX == emptyX)) { //Vertical Move
            if (currentY < emptyY) { // Above
                swapEmptyTile(tileMatrix, emptyTileRowIndex - 1, emptyTileColIndex);
            } else { // Below
                swapEmptyTile(tileMatrix, emptyTileRowIndex + 1, emptyTileColIndex);
            }
            //Code that moves the TextViews
            tile.animate().x(emptyX).y(emptyY);
            emptyTileButton.animate().x(currentX).y(currentY);
        }
    }

    // Manipulates the AIBoard in the UI
    // Swaps the tile with the number 'tileNumber' with the empty tile
    public void moveAITile(int tileNumber){
        //Do Something
        Button emptyTile = AIBoardMap.get(-1);
        Button tile = AIBoardMap.get(tileNumber);

        float currentX = tile.getX();
        float currentY = tile.getY();
        float emptyY = emptyTile.getY();
        float emptyX = emptyTile.getX();

        //Code that moves the Tiles
        tile.animate().x(emptyX).y(emptyY);
        emptyTile.animate().x(currentX).y(currentY);
    }

    // Swap the tile located at matrix[row][col] with the 'empty tile'
    // SHOULD ONLY BE USED FOR THE PLAYERS BOARD
    public void swapEmptyTile(int [][] matrix, int row, int col) {
        int temp = matrix[row][col];
        matrix[row][col] = matrix [emptyTileRowIndex][emptyTileColIndex];
        matrix[emptyTileRowIndex][emptyTileColIndex] = temp;
        // Update the position of the empty tile
        emptyTileRowIndex = row;
        emptyTileColIndex = col;
    }

    // Swap the tile located at matrix[row1][col1] with matrix[row2][col2]
    // SHOULD NOT BE USED FOR THE PLAYERS BOARD
    public void swap(int [][] matrix, int row1, int col1, int row2, int col2) {
        int temp = matrix[row1][col1];
        matrix[row1][col1] = matrix [row2][col2];
        matrix[row2][col2] = temp;
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

    // Copies a 2d Board Matrix
    private void copyMatrix(int[][] src, int[][] dst) {
        for (int i = 0; i < 5; i++) {
            System.arraycopy(src[i], 0, dst[i], 0 , 5);
        }
    }
}

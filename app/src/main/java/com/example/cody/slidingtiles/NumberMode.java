package com.example.cody.slidingtiles;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.TextView;
import android.os.Handler;
import android.os.SystemClock;
import android.view.ViewGroup.LayoutParams;


public class NumberMode extends AppCompatActivity {

    //Board Resources
    int tileMatrix[][] = new int [5][5];
    float xTileDistance = 0;
    float yTileDistance = 0;
    int emptyTileRowIndex;
    int emptyTileColIndex;

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
    private ConstraintLayout mRelativeLayout;


    //UI Elements
    Button emptyTileButton;
    GridLayout board;

    //Helper Classes
    BoardGenerator boardGen = new BoardGenerator();


//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        timeSwapBuff += timeInMilliseconds;
//        customHandler.removeCallbacks(updateTimerThread);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_mode);
        //back_dim_layout = (ConstraintLayout) findViewById(R.id.bac_dim_layout);

        //Popup
        mContext = getApplicationContext();
        mRelativeLayout = (ConstraintLayout) findViewById(R.id.rl);

        // Timer implementation
        timerValue = (TextView) findViewById(R.id.timerValue);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

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
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );

                mPopupWindow.setTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setOutsideTouchable(false);
//

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
                //customView.getWindowToken();
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);


            }
        });

        //Create a 2-D array of the board
        tileMatrix = boardGen.generateNumberModeBoard();

        //Move the contents of the 2-D array to the UI
        board = findViewById(R.id.board);
        displayBoardMatrixUI(board);
    }

    // Timer code
    private Runnable updateTimerThread = new Runnable() {
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
                swap(tileMatrix, emptyTileRowIndex, emptyTileColIndex + 1);
            } else { // Left
                swap(tileMatrix, emptyTileRowIndex, emptyTileColIndex - 1);
            }
            //Code that moves the TextViews
            tile.animate().x(emptyX).y(emptyY);
            emptyTileButton.animate().x(currentX).y(currentY);
        } else if ((Math.abs(currentY - emptyY) == yTileDistance) && (currentX == emptyX)) { //Vertical Move
            if (currentY < emptyY) { // Above
                swap(tileMatrix, emptyTileRowIndex - 1, emptyTileColIndex);
            } else { // Below
                swap(tileMatrix, emptyTileRowIndex + 1, emptyTileColIndex);
            }
            //Code that moves the TextViews
            tile.animate().x(emptyX).y(emptyY);
            emptyTileButton.animate().x(currentX).y(currentY);
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

}

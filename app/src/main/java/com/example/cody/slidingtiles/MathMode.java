package com.example.cody.slidingtiles;

import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.os.Handler;
import android.os.SystemClock;

public class MathMode extends AppCompatActivity {

    //Board Resources
    int tileMatrix[][] = new int [5][5];
    float xTileDistance = 0;
    float yTileDistance = 0;
    int currentScore = 0;

    // Timer variables
    private Button startButton;
    private Button pauseButton;
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    //UI Elements
    Button emptyTileButton;
    GridLayout board;
    ViewGroup submissionHistoryWindow;

    //Helper Classes
    MathSolutionHandler equationHandler = new MathSolutionHandler();
    BoardGenerator boardGen = new BoardGenerator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_mode);

        // Timer implementation
        timerValue = (TextView) findViewById(R.id.timerValue);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
            }
        });
        pauseButton = (Button) findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
            }
        });

        //Create a 2-D array of the board
        tileMatrix = boardGen.generateMathModeBoard();
        tileMatrix[4][4] = -1;  //Set this as the blank tile
    //    shuffleBoard(tileMatrix);

        //Move the contents of the 2-D array to the UI
        board = findViewById(R.id.board);
        displayBoardMatrixUI(board);

        //Find and active the shuffle button
        Button btnShuffle = findViewById(R.id.btnShuffle);
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
            if (score == -1) {
                submission.setTextColor(Color.RED);
            } else if(score == 0 ) {
                submission.setTextColor(Color.BLUE);
            } else if(score == -2 ) {
                submission.setTextColor(Color.YELLOW);
            } else {
                submission.setTextColor(Color.GREEN);
                updateScore(score);
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

    // Force he board to be shuffled and redisplayed.
    public void DemandShuffle(int tileMatrix[][]){
        boardGen.shuffleBoard(tileMatrix);
        displayBoardMatrixUI(board);
    }

    private void updateScore(int score){
        currentScore += score;
        TextView playerScore = findViewById(R.id.currentScoreTextView);
        playerScore.setText(String.valueOf(currentScore));
    }
}



package com.example.cody.slidingtiles;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.os.Handler;
import android.os.SystemClock;

import java.io.IOException;
import java.nio.charset.Charset;

public class MathMode2Player extends AppCompatActivity {
    private final String TAG = "MathMode2Player";
    //Board Resources
    int tileMatrix[][] = new int [5][5];
    float xTileDistance = 0;
    float yTileDistance = 0;
    private float ySubmittedTile;
    private float xSubmittedTile;
    private int axisLock;   // 1 = Vertical solution; 2 = Horizontal solution

    // GAME STATE MANAGEMENT
    int numberOfGames;
    int currentGameNumber;
    int playerWins = 0;
    int opponentWins = 0;
    int opponentScore = 0;
    int currentScore = 0;

    //UI Elements
    Button emptyTileButton;
    GridLayout board;
    ViewGroup submissionHistoryWindow;

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
    final Context context = this;
    private Context mContext;
    private PopupWindow mPopupWindow;
    private ConstraintLayout mRelativeLayout;

    //Helper Classes
    MathSolutionHandler equationHandler = new MathSolutionHandler();
    BoardGenerator boardGen = new BoardGenerator();

    //bluetooth communication
    public StringBuilder messages;
    TextView incomingMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_mode);

        //input stream
        //incomingMessages = new TextView(this);
        messages = new StringBuilder();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

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
                byte[] bytes = "Pause".getBytes(Charset.defaultCharset());
                writeWrapper(bytes);
                pausefunction();
                /*
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);


                //popup
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.popup,null);
                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                );
                //mPopupWindow.setFocusable(true);
                //mPopupWindow.update();
                //mPopupWindow.setOutsideTouchable(false);
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
*/

            }
        });

        //Create a 2-D array of the board

        try {
            Intent intent = getIntent();
            String boardString = intent.getStringExtra("newGame");
            tileMatrix = boardGen.mathModeBoardFromString(boardString);
        }catch (Exception e){
            try{
                tileMatrix = boardGen.generateMathModeBoard();
            }catch (Exception e1){
                Log.e(TAG, "error creating default board");
            }
            Log.e(TAG, "error creating shared board");
        }
//        shuffleBoard(tileMatrix);

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
    // Calculates distances using tiles located in the lower right corner of the board.
    private void obtainTileDistance() {
        View xButton = findViewById(R.id.xButton);
        View yButton = findViewById(R.id.yButton);
        View lowerRightButton = findViewById(R.id.lowerRightButton);

        xTileDistance = Math.abs(xButton.getX() - lowerRightButton.getX());
        yTileDistance = Math.abs(yButton.getY() - lowerRightButton.getY());
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
            if (equationHandler.getCountOfSubmittedTiles() != 0) {
                int score = equationHandler.solve();
                if (score == -1) {
                    submission.setTextColor(Color.RED);
                } else if(score == 0 ) {
                    submission.setTextColor(Color.BLUE);
                } else if(score == -2 ) {
                    submission.setTextColor(Color.YELLOW);
                } else {
                    submission.setTextColor(Color.GREEN);
//!!!!                    //send to player 2
                    byte[] bytes = equationHandler.getEquationString().getBytes(Charset.defaultCharset());
                    writeWrapper(bytes);
                    updateScore(score);
                }
                submission.setTextSize(20);
                submission.setBackgroundColor(Color.GRAY);
                submission.setText(equationHandler.getEquationString());
                submissionHistoryWindow.addView(submission, 0);
                equationHandler.resetHandler();
            }
            return true;
        } else {
            Button tile = (Button) findViewAt(board, xPos, yPos);
            if (tile != null && (equationHandler.getCountOfSubmittedTiles() != 5)) {
                // If this is the first time we are calling this, lets get the tile distances
                if (xTileDistance == 0) {
                    obtainTileDistance();
                }
                // The following logic only allows horizontal or vertical solutions
                int tileCount = equationHandler.getCountOfSubmittedTiles();
                if (tileCount == 0) {
                    //This if the first submitted tile, lets get the x,y coords
                    xSubmittedTile = tile.getX();
                    ySubmittedTile = tile.getY();
                    equationHandler.addTile(tile);
                }else if(tileCount == 1) {
                    //This is the second tile we are attempting to add.
                    //Make sure this tile is next to the first tile
                    //Also depending on its location (above/below or left/right) lock future submissions to either horizontal or vertical
                    if ((Math.abs(tile.getY() - ySubmittedTile) == yTileDistance) && (tile.getX() == xSubmittedTile)) {
                        //Vertical Submission
                        axisLock = 1;
                        ySubmittedTile = tile.getY();
                        equationHandler.addTile(tile);
                    }else if ((Math.abs(tile.getX() - xSubmittedTile) == xTileDistance) && (tile.getY() == ySubmittedTile)) {
                        //Horizontal Submission
                        axisLock = 2;
                        xSubmittedTile = tile.getX();
                        equationHandler.addTile(tile);
                    }
                } else {
                    //We are attempting to add tiles 3-5
                    if (axisLock == 1) {    //Vertical Submission
                        //Check to see if this solution is directly above or below the previous tile
                        if ((Math.abs(tile.getY() - ySubmittedTile) == yTileDistance) && (tile.getX() == xSubmittedTile)) {
                            ySubmittedTile = tile.getY();
                            equationHandler.addTile(tile);
                        }
                    }else {                 //Horizontal Submission
                        if ((Math.abs(tile.getX() - xSubmittedTile) == xTileDistance) && (tile.getY() == ySubmittedTile)) {
                            xSubmittedTile = tile.getX();
                            equationHandler.addTile(tile);
                        }
                    }
                }
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

        float emptyY = emptyTileButton.getY();
        float emptyX = emptyTileButton.getX();

        if (((Math.abs(currentX - emptyX) == xTileDistance) && (currentY == emptyY)) || ((Math.abs(currentY - emptyY) == yTileDistance) && (currentX == emptyX))) {
            //Code that moves the TextViews
            tile.animate().x(emptyX).y(emptyY);
            emptyTileButton.animate().x(currentX).y(currentY);
        }
    }

    // Force he board to be shuffled and redisplayed.
    public void DemandShuffle(int tileMatrix[][]){
        boardGen.shuffleBoard(tileMatrix);
        displayBoardMatrixUI(board);
    }

    // Updates the score accordingly
    private void updateScore(int score){
        currentScore += score;
        TextView playerScore = findViewById(R.id.currentScoreTextView);
        playerScore.setText(String.valueOf(currentScore));
    }

    // wrapper for writing to the output stream
    public void writeWrapper(byte[] bytes){
        //Log.d(TAG, "writing out.");
        ((BaseApp)this.getApplicationContext()).myBtConnection.write(bytes);
    }

    //Pause function.
    public void pausefunction(){
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);


        // -------------------------- dialouge popup -------------------------//
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        //dialog.setTitle("Title.");

        Button resumeButton = (Button) dialog.findViewById(R.id.resume);
        Button closeButton = (Button) dialog.findViewById(R.id.exit);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes = "Exit".getBytes(Charset.defaultCharset());
                writeWrapper(bytes);
                //finish();
                //System.exit(0);
                // -------------------------- inside dialog ---------------------------- //
                // custom dialog
                final Dialog dialog1 = new Dialog(context);
                dialog1.getWindow().setGravity(Gravity.CENTER);
                dialog1.setContentView(R.layout.popup1);
                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog1.setCanceledOnTouchOutside(false);

                //dialog.setTitle("Title.");
                TextView scoreView = (TextView) dialog1.findViewById(R.id.player_score);
                TextView playerWin = (TextView) dialog1.findViewById(R.id.player_win) ;
                Button closeButton1 = (Button) dialog1.findViewById(R.id.exit1 );

                closeButton1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        finish();
                        System.exit(0);

                    }
                });

                dialog1.show();
                // -------------------------- inside dialog end---------------------------- //
            }
        });
        resumeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                byte[] bytes = "Resume".getBytes(Charset.defaultCharset());
                writeWrapper(bytes);
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                mPopupWindow.dismiss();
            }
        });
        dialog.show();
        // -------------------------- dialogue popup end ---------------------//
    }
    //resume function
    public void resumeFunction() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        mPopupWindow.dismiss();
    }
    //resume function
    public void exitFunction() {
        finish();
        System.exit(0);
    }
    //get input stream
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "reading in.");
            String text = intent.getStringExtra("theMessage");
            if(text.contains("Pause")){
                pausefunction();
            }else if(text.contains("Resume")){
                resumeFunction();
            }else if(text.contains("Exit")){
                exitFunction();
            }else{
                TextView submission = new TextView(context);
                submission.setTextSize(20);
                submission.setBackgroundColor(Color.GRAY);
                submission.setTextColor(Color.MAGENTA);
                submission.setText(text);
                submissionHistoryWindow.addView(submission, 0);
                equationHandler.addToSolutionBlackList(text);
            }
        }
    };
}



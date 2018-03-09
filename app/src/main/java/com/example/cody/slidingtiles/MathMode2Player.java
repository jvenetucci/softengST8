package com.example.cody.slidingtiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.widget.Toast;

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
    private String gameMode;
    private static final String BASIC_MODE = "BSC";
    private static final String CUTTHROAT_MODE = "CUT";
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
    private Context mContext;
    private PopupWindow mPopupWindow;
    private ConstraintLayout mRelativeLayout;

    //Helper Classes
    MathSolutionHandler equationHandler = new MathSolutionHandler();
    BoardGenerator boardGen = new BoardGenerator();
    MathSolutionHandler playerEQHandler = new MathSolutionHandler();
    MathSolutionHandler opponentEQHandler = new MathSolutionHandler();

    //bluetooth communication
    public StringBuilder messages;
    TextView incomingMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_mode_cutthroat);

        //input stream
        //incomingMessages = new TextView(this);
        messages = new StringBuilder();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

        //Initialize the game state:
        //Create a 2-D array of the board
        //Set Rounds
        try {
            Intent intent = getIntent();
            String boardString = intent.getStringExtra("newGame");
            tileMatrix = boardGen.mathModeBoardFromString(boardString);
            numberOfGames = intent.getIntExtra("rounds", 1);
            currentGameNumber = intent.getIntExtra("gameNumber", 1);
            gameMode = intent.getStringExtra("gameType");
            TextView numberOfRounds = (TextView) findViewById(R.id.roundCount);
            numberOfRounds.setText("Round: "+String.valueOf(currentGameNumber) + " / " + String.valueOf(numberOfGames));
            playerWins = intent.getIntExtra("playWin", 0);
            TextView playerWinsTextView = (TextView) findViewById(R.id.playerWinCount);
            playerWinsTextView.setText(String.valueOf(playerWins));
            opponentWins = intent.getIntExtra("oppWin", 0);
            TextView opponentWinsTextView = (TextView) findViewById(R.id.opponentWinCount);
            opponentWinsTextView.setText(String.valueOf(opponentWins));
            TextView playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
            playerNameTextView.setText(((BaseApp)this.getApplicationContext()).playerName +": ");
            TextView opponentNameTextView = (TextView) findViewById(R.id.opponentNameTextView);
            ((BaseApp)this.getApplicationContext()).opponentName =intent.getStringExtra("oppName");
            opponentNameTextView.setText(((BaseApp)this.getApplicationContext()).opponentName +": ");
        }catch (Exception e){
            try{
                tileMatrix = boardGen.generateMathModeBoard();
            }catch (Exception e1){
                Log.e(TAG, "error creating default board");
            }
            Log.e(TAG, "error creating shared board");
        }

        //Popup
        mContext = ((BaseApp)this.getApplicationContext());
        mRelativeLayout = (ConstraintLayout) findViewById(R.id.rl);


        // Timer implementation
        timerValue = (TextView) findViewById(R.id.timerValue);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        // TEST WITH ONLY NEXT ROUND BUTTON
/*/
        Button nextRoundButton = (Button) findViewById(R.id.pauseButton);
        nextRoundButton.setText("Next Round");
        nextRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextRoundActivity();
            }
        });
//*/
//*      Remove the Pause pop-up completely??
        pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                byte[] bytes = "Pause".getBytes(Charset.defaultCharset());
                writeWrapper(bytes);

                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);


                //popup
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.popup2,null);
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
                Button nextRoundButton = (Button) customView.findViewById(R.id.nextRound);

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        byte[] bytes = "Exit".getBytes(Charset.defaultCharset());
                        writeWrapper(bytes);
                        finish();
                        System.exit(0);
                    }
                });
                resumeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);
                        mPopupWindow.dismiss();
                        byte[] bytes = "Resume".getBytes(Charset.defaultCharset());
                        writeWrapper(bytes);
                    }
                });
                nextRoundButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        nextRoundActivity();
                    }
                });
                //customView.getWindowToken();
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);


            }
        });
//*/

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
                if (score == -1) {          // Invalid equation
                    submission.setTextColor(Color.RED);
//                } else if(score == 0 ) {
//                    submission.setTextColor(Color.BLUE);
                } else if(score == -2 ) {   // Incorrect format
                    submission.setTextColor(Color.YELLOW);
                } else if (score == -3) {   // Already Used
                    submission.setTextColor(Color.DKGRAY);
                } else {
                    submission.setTextColor(Color.GREEN);
//!!!!                    //send to player 2 and add to our own blacklist
                    playerEQHandler.addToSolutionBlackList(equationHandler.getEquationString());
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

    // Updates the PLAYER score accordingly
    private void updateScore(int score){
        currentScore += score;
        TextView playerScore = findViewById(R.id.playerScoreTextView);
        playerScore.setText(String.valueOf(currentScore));
    }
    // Updates the OPPONENT score accordingly
    private void updateOppScore(int score){
        Log.d(TAG, "updating opp score.. was: " + opponentScore + " + this: " + score);
        opponentScore += score;
        TextView playerScore = findViewById(R.id.opponentScoreTextView);
        playerScore.setText(String.valueOf(opponentScore));
    }

    // wrapper for writing to the output stream
    public void writeWrapper(byte[] bytes){
        //Log.d(TAG, "writing out.");
        ((BaseApp)this.getApplicationContext()).myBtConnection.write(bytes);
    }



//*    //Pause function.
    public void pauseFunction(){

        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);


        //popup
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup2,null);
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
        Button nextRoundButton = (Button) customView.findViewById(R.id.nextRound);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                byte[] bytes = "Exit".getBytes(Charset.defaultCharset());
                writeWrapper(bytes);
                finish();
                System.exit(0);
            }
        });
        resumeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                mPopupWindow.dismiss();
                byte[] bytes = "Resume".getBytes(Charset.defaultCharset());
                writeWrapper(bytes);
            }
        });
        nextRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                nextRoundActivity();
            }
        });
        //customView.getWindowToken();
        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);


    }
//*/
    //resume function
    public void resumeFunction() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        mPopupWindow.dismiss();
    }
    //exit function
    public void exitFunction() {
        mPopupWindow.dismiss();
        finish();
        System.exit(0);
    }


    // UPDATE THE UI FOR NEW ROUNDS
    // Resets game board
    public void updateUI(String boardString){

        try {

            // Timer implementation
            timeSwapBuff = 0L;
            timerValue = (TextView) findViewById(R.id.timerValue);
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);

            tileMatrix = boardGen.mathModeBoardFromString(boardString);

            TextView numberOfRounds = (TextView) findViewById(R.id.roundCount);
            numberOfRounds.setText("Round: "+String.valueOf(currentGameNumber) + " / " + String.valueOf(numberOfGames));

            TextView playerWinsTextView = (TextView) findViewById(R.id.playerWinCount);
            playerWinsTextView.setText(String.valueOf(playerWins));

            TextView opponentWinsTextView = (TextView) findViewById(R.id.opponentWinCount);
            opponentWinsTextView.setText(String.valueOf(opponentWins));

            currentScore = 0;
            TextView playerScore = findViewById(R.id.playerScoreTextView);
            playerScore.setText(String.valueOf(currentScore));

            opponentScore = 0;
            TextView oppScore = findViewById(R.id.opponentScoreTextView);
            oppScore.setText(String.valueOf(opponentScore));


            submissionHistoryWindow.removeAllViews();
        }catch (Exception e) {
            Log.d(TAG, "Fail to update UI");
        }
        board = findViewById(R.id.board);
        displayBoardMatrixUI(board);
        equationHandler.clearSolutionBlacklist();
        opponentEQHandler.clearSolutionBlacklist();
        playerEQHandler.clearSolutionBlacklist();
    }

    //nextRound function
    //Determine the winner.
    //If we are at the max number of games, display the final winner.
    public void nextRoundActivity(){
        if(currentScore > opponentScore){
            playerWins++;
        }else if(opponentScore > currentScore){
            opponentWins++;
        }
        if(currentGameNumber == numberOfGames){
            if(playerWins > opponentWins){
                Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_LONG).show();
            }
            boolean connectStatus = ((BaseApp) this.getApplicationContext()).myBtConnection.getState();
            if (connectStatus) {
                Log.d(TAG, "Next Round: Ending Game ");
                try {
                    String gameStart = "Next Round";
                    byte[] bytes = gameStart.getBytes(Charset.defaultCharset());
                    ((BaseApp) this.getApplicationContext()).myBtConnection.write(bytes);
                } catch (Exception e) {
                    Log.d(TAG, "new activity: fail to End game over input stream");
                }
                finish();
                System.exit(0);
            }
        }else {
            currentGameNumber++;
            String sharedBoardAsString = boardGen.boardToString(boardGen.generateMathModeBoard());
            Log.d(TAG, "Next Round: " +sharedBoardAsString);
            boolean connectStatus = ((BaseApp) this.getApplicationContext()).myBtConnection.getState();
            if (connectStatus) {
                Log.d(TAG, "Next Round: connected " );
                try {
                    String gameStart = "Next Round";
                    gameStart += playerWins;
                    gameStart += opponentWins;
                    gameStart += gameMode;
                    gameStart += sharedBoardAsString;
                    Log.d(TAG, "new Activity: write out all: " +gameStart);
                    byte [] bytes =  gameStart.getBytes(Charset.defaultCharset());
                    ((BaseApp) this.getApplicationContext()).myBtConnection.write(bytes);
                }catch (Exception e){
                    Log.d(TAG, "new activity: fail to send game over input stream");
                }
                updateUI(sharedBoardAsString);
            } else {
                Log.d(TAG, "new activity: NOT connected " );
            }
        }

    }

    //get input stream
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "reading in.");
            int scoreToAdd = 0;
            String text = intent.getStringExtra("theMessage");
            if(text.contains("Pause")){
                pauseFunction();
            }else if(text.contains("Resume")){
                resumeFunction();
            }else if(text.contains("Exit")){
                exitFunction();
            }
            else if(text.contains("Next Round")) {
                mPopupWindow.dismiss();
                if(currentScore > opponentScore){
                    playerWins++;
                }else if(opponentScore > currentScore){
                    opponentWins++;
                }
                if (currentGameNumber == numberOfGames) {
                    if (playerWins > opponentWins) {
                        Toast.makeText(context, "Player 1 wins!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Player 2 wins!", Toast.LENGTH_LONG).show();
                    }
                    finish();
                    System.exit(0);
                } else {
                    currentGameNumber++;
                    if (text.contains("BSC")) {
                        gameMode = "BSC";
                    } else {
                        gameMode = "CUT";
                    }
                    // swap player and opponent win count since its player perspective
                    playerWins = Integer.valueOf(text.substring(11, 12));
                    opponentWins = Integer.valueOf(text.substring(10, 11));
                    String newBoard = text.substring(15);
                    updateUI(newBoard);
                }
            }

            else{

                //equationHandler.addToSolutionBlackList(text);
                int[] oppEquation = new int[5];
                for(int i = 0; i < 5; i++) {
                    char temp = text.charAt(i*2);
                    Log.d(TAG, "temp is: " +temp);
                    switch (temp) {
                        case '=':
                            oppEquation[i] = 10;
                            break;
                        case '+':
                            oppEquation[i] = 11;
                            break;
                        case '-':
                            oppEquation[i] = 12;
                            break;
                        case '*':
                            oppEquation[i] = 13;
                            break;
                        case '/':
                            oppEquation[i] = 14;
                            break;
                        default:
                            oppEquation[i] = Character.getNumericValue(text.charAt(i * 2));
                    }
                }
                // CUTthroat means we solutions are usable only once by either player
                // BSC means solutions are individual and we should not show it
                if(gameMode.compareTo("CUT") ==0){
                    TextView submission = new TextView(context);
                    submission.setTextSize(20);
                    submission.setBackgroundColor(Color.GRAY);
                    submission.setTextColor(Color.MAGENTA);
                    submission.setText(text);
                    submissionHistoryWindow.addView(submission, 0);
                    opponentEQHandler.addToSolutionBlackList(text);
                    Log.d(TAG, "reading in equation: " + oppEquation[0]+ "|" + oppEquation[1]+"|" +oppEquation[2]+"|" +oppEquation[3]+"|" +oppEquation[4]);
                    scoreToAdd = equationHandler.solveEquation(oppEquation);
                }else{
                    scoreToAdd = opponentEQHandler.solveEquation(oppEquation);
                }

                updateOppScore(scoreToAdd);
            }
        }
    };
}



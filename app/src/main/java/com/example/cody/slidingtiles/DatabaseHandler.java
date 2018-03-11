package com.example.cody.slidingtiles;


import android.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A class that handles the connect with Firebase.
 * This class allows you to get database references
 * and also enables you to update the database.
 */

public class DatabaseHandler {
    private FirebaseDatabase databaseInstance;
    private DatabaseReference databaseRef;
    private DatabaseReference mathModeRef;

    private Record records [];
    private int lowestScore;
    private int lowestScoreIndex;

    TableLayout highScoreUIMath;


    // Constructor
    DatabaseHandler() {
        databaseInstance = FirebaseDatabase.getInstance();
        databaseRef = databaseInstance.getReference();
        mathModeRef = databaseRef.child("MathMode");
        queryTopFiveScores();
        highScoreUIMath = null;
    }

    // Constructor for Highscores UI
    DatabaseHandler(TableLayout table) {
        highScoreUIMath = table;
        databaseInstance = FirebaseDatabase.getInstance();
        databaseRef = databaseInstance.getReference();
        mathModeRef = databaseRef.child("MathMode");
        queryTopFiveScores();
    }

    // Add the record to the high score list at the key "index".
    void writeToMathMode(int index, String name, int score) {
        Record r = new Record(name, score);
        mathModeRef.child(String.valueOf(index)).setValue(r);
    }

    // Add the record to the high score list at the key "index".
    void writeToMathMode(int index, Record record) {
        mathModeRef.child(String.valueOf(index)).setValue(record);
    }

    // Add a user and score to the high score list.
    void pushToMathMode(String name, int score) {
        Record r = new Record(name, score);
        pushToMathMode(r);
    }

    // Add a user and score to the high score list.
    void pushToMathMode(Record r) {
        DatabaseReference pushRef = mathModeRef.push();
        pushRef.setValue(r);
    }

    // Retrieve the top 5 scores and place them in the Records Array
    // Array is filled with the 5th highest score at [0], 2nd at [1], and so on.
    // If there are < 5 scores, the remainder of the array is padded with null records.
    void queryTopFiveScores() {
        Query topFive = mathModeRef.orderByChild("score").limitToLast(5);
        topFive.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                records = new Record[5];
                int i = 0;
                for (DataSnapshot dbRecord: dataSnapshot.getChildren()) {
                    Record  rdb = dbRecord.getValue(Record.class);
                    records[i] = new Record(rdb.getName(), rdb.getScore());
                    i++;
                    Log.d("TSS", "onDataChange: " + dbRecord.getKey());
                }
                for (int j = i; j < 5; j++) {
                    records[j] = null;
                }
                lowestScore = records[0].getScore();

                if (highScoreUIMath != null) {
                    mapScoresToUI();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    boolean checkForNewMathHighScore(int score) {
        if(score > lowestScore) {
            return true;
        } else {
            return false;
        }
    }

    @Deprecated
    private void updateLowestMathScore() {
        int index = 4;
        while (index >= 0 && records[index] == null){
            index--;
        }
        if (index < 0) {
            lowestScore = 0;
            lowestScoreIndex = 0;
        } else {
            lowestScore = records[index].getScore();
            lowestScoreIndex = index;
        }
    }

    @Deprecated
    // Need to do it this way because whenever the DB gets updated the lowest score and records array
    // changes.
    public void addNewMathHighScore(String name, int score) {
        addNewMathHighScore(name, score, records, lowestScoreIndex);
    }

    @Deprecated
    private void addNewMathHighScore(String name, int score, Record[] records, int lowestScoreIndex) {
        Record r = new Record(name, score);
        Record recordsToWrite [] = new Record[5];

        //Find which spot (1-5) the new high score goes in.
        int index = lowestScoreIndex;
        while (index >= 0 && (records[index].getScore() < score)) {
            index --;
        }

        //Update
        index++;
        writeToMathMode(index, r);
        for (int i = index; i < 4; i++ ) {
            if (records[i] != null) {
                writeToMathMode((i+1), records[i]);
            }
        }
    };

    @Deprecated
    public void readMathScore() {
//        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                records = new Record[5];
//                for (int i = 0; i < 5; i++) {
//                    Record post = dataSnapshot.child("MathMode").child(String.valueOf(i)).getValue(Record.class);
//                    if (post != null) {
//                        records[i] = new Record(post.getName(), post.getScore());
//                    } else {
//                        records[i] = null;
//                    }
//                }
//                updateLowestMathScore();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    public void updateHighScoreUI(TableLayout mathModeTable) {
        highScoreUIMath = mathModeTable;
//        mapScoresToUI();
    }

    private void mapScoresToUI() {
        if (highScoreUIMath != null) {
            TextView col;
            TableRow row;
            int i = 4;
            while (records[i] == null) {
                i--;
            }
            for (int j = 2; i >= 0; j++, i--) {
                row = (TableRow) highScoreUIMath.getChildAt(j);
                col = (TextView) row.getChildAt(0);
                col.setText(records[i].getName());
                col = (TextView) row.getChildAt(1);
                col.setText(Integer.toString(records[i].getScore()));
            }
        }
    }
}

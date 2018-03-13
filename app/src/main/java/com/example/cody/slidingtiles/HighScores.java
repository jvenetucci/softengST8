package com.example.cody.slidingtiles;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;

public class HighScores extends AppCompatActivity {

    private TextView mScore;
    private TextView nScore;
    DatabaseHandler db;
    TableLayout mathModeTable;
    //private DatabaseReference mRef;
    //private DatabaseReference nRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

//        mScore = (TextView) findViewById(R.id.textView22);
        nScore = (TextView) findViewById(R.id.ntextView22);
        mathModeTable = findViewById(R.id.mathModeTable);

//        // Firebase database
//        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
//        final DatabaseReference mRef = mDatabase.getReference("User/Math_mode");
//        final DatabaseReference nRef = mDatabase.getReference("User/Number_mode");
        db = new DatabaseHandler(mathModeTable);
//        db.updateHighScoreUI(mathModeTable);


//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
//                Double value1 = dataSnapshot.getValue(Double.class);
//                NumberFormat nm = NumberFormat.getNumberInstance();
//                mScore.setText(nm.format(value1));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//        mRef.addValueEventListener(postListener);

//        ValueEventListener postListener1 = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
//                Double value2 = dataSnapshot.getValue(Double.class);
//                NumberFormat nm = NumberFormat.getNumberInstance();
//                nScore.setText(nm.format(value2));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//        nRef.addValueEventListener(postListener1);
    }
}

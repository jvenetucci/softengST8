package com.example.cody.slidingtiles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //this function is called when number mode button is tapped
    public void startNumberMode(View view) {
        Intent intent = new Intent(this, NumberMode.class);
        startActivity(intent);
    }

    public void startMathMode(View view) {
        Intent intent = new Intent(this, MathMode.class);
        startActivity(intent);
    }

    public void startHighScores(View view) {
        Intent intent = new Intent(this, HighScores.class);
        startActivity(intent);
    }
}

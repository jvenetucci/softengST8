package com.example.cody.slidingtiles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterName extends AppCompatActivity {

    private EditText mMessageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_name);

        mMessageEditText = (EditText) findViewById(R.id.enter_name);
        Button mSendData = (Button) findViewById(R.id.button2);

        mSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mMessageEditText.getText().toString();

                addNameToBaseApp(username);

                Intent intent = new Intent(EnterName.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Set the player name is base app to what the user entered.
    // If they left it blank, uses the default name of 'Player 1'
    private void addNameToBaseApp(String userName) {
        if (userName.compareTo("") != 0) {
            ((BaseApp)this.getApplicationContext()).playerName = userName;
        }
    }
}

package com.example.cody.slidingtiles;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class    EnterName extends AppCompatActivity {

    private Button mSendData;
    private EditText mMessageEditText;
    private TextView mkey;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_name);

        // Firebase database
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDatabase.getReference("User");

        mMessageEditText = (EditText) findViewById(R.id.enter_name);
        mSendData = (Button) findViewById(R.id.button2);
        mkey = (TextView) findViewById(R.id.key_name);

        mSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString();
                String key =  mkey.getText().toString();

                DatabaseReference chilRef = myRef.child(key);
                //chilRef.child(key).setValue(message);
                chilRef.push().setValue(message);
                Intent intent = new Intent(EnterName.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}

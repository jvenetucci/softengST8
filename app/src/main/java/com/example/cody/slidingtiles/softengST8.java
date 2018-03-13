package com.example.cody.slidingtiles;
import android.app.Application;

import com.google.firebase.FirebaseApp;


/**
 * Created by amarjit on 3/6/18.
 */

public class softengST8 extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
    }
}

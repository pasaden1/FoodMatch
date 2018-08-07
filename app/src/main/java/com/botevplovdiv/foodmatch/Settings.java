package com.botevplovdiv.foodmatch;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class Settings extends AppCompatActivity {
    String TAG = "FoodMatch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //lock the device to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.i(TAG,"Entered onCreate() method in SActivity");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"Entered onStart() method in SActivity");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"Entered onRestart() method in SActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"Entered onResume() method in SActivity");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"Entered onPause() method in SActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"Entered onStop() method in SActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Entered onDestroy() method in SActivity");
    }

}




















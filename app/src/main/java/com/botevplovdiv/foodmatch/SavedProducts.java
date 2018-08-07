package com.botevplovdiv.foodmatch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedProducts extends AppCompatActivity {
    String TAG = "FoodMatch";

    private ListView savedProductsView;
    private MyListProductsAdapter listProductsAdapter;
    private List<FoodDish> savedProductsList;
    MenuItem clearListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //lock the device to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();


        savedProductsList = new ArrayList<>();
        savedProductsList = bundle.getParcelableArrayList("likedProducts");
        HashMap<Long, Venue> venueList = (HashMap<Long, Venue>) intent.getSerializableExtra("venueList");


        // create ArrayAdapter to bind savedProductsList to the savedProductsView
        savedProductsView = (ListView) findViewById(R.id.savedProductsList);
        listProductsAdapter = new MyListProductsAdapter(this,savedProductsList,venueList);
        savedProductsView.setAdapter(listProductsAdapter);
        Log.i(TAG,"Entered onCreate() method in SavedProducts");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_savedproducts,menu);
        clearListButton = menu.findItem(R.id.clearListbutton);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == clearListButton){
            if (!savedProductsList.isEmpty()){
                new AlertDialog.Builder(this)
                        .setTitle("Clear List?")
                        .setMessage("Are you sure you want to clear all the entries?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                savedProductsList.clear();
                                MainActivity.likedProductsList.clear();
                                listProductsAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }else{
                Toast.makeText(this,"The list is empty",Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"Entered onStart() method in SavedProducts");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"Entered onRestart() method in SavedProducts");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"Entered onResume() method in SavedProducts");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"Entered onPause() method in SavedProducts");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"Entered onStop() method in SavedProducts");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Entered onDestroy() method in SavedProducts");
    }

}

package com.botevplovdiv.foodmatch;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    FoodDish currentDish;
    Venue currentVenue;
    TextView foodTitle;
    TextView calculatedTotalPrice;
    TextView venueName;
    TextView venueCategory;
    TextView venueAddress;
    TextView venuePhone;
    ImageButton decrease;
    ImageButton increase;
    TextView quantity;
    int quantityAmount;
    double totalPriceCalculation;
    EditText editTextName;
    EditText editTextAddress;
    EditText editTextPhone;
    Button placeOrder;
    ImageView venueImage;
    ImageButton phoneButton;
    ImageButton mapButton;
    CardView detailsCard;

    private FirebaseDatabase mDataBase;
    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //lock the device to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle extras = getIntent().getExtras();

        mDataBase = FirebaseDatabase.getInstance();
        databaseReference = mDataBase.getReference();

        currentDish =extras.getParcelable("currentDish");
        currentVenue = extras.getParcelable("currentVenue");
        foodTitle = (TextView) findViewById(R.id.foodItemTitle);
        calculatedTotalPrice = (TextView) findViewById(R.id.calculatedTotal);
        decrease = (ImageButton) findViewById(R.id.decreseArrow);
        increase = (ImageButton) findViewById(R.id.increseArrow);
        quantity = (TextView) findViewById(R.id.quantityText);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        placeOrder = (Button) findViewById(R.id.placeOrderButton);
        venueImage = (ImageView) findViewById(R.id.venueImage);
        venueName = (TextView) findViewById(R.id.venueName);
        venueAddress = (TextView) findViewById(R.id.venuAddress);
        venueCategory = (TextView) findViewById(R.id.venueCategory);
        venuePhone = (TextView) findViewById(R.id.phoneNumber);
        phoneButton = (ImageButton) findViewById(R.id.phoneButton);
        mapButton = (ImageButton) findViewById(R.id.mapButton);
        detailsCard = (CardView)findViewById(R.id.detailsCard);


        quantityAmount = 1;

        foodTitle.setText(currentDish.getTitle());
        quantity.setText(String.valueOf(quantityAmount));

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityAmount>0){
                    quantityAmount--;
                    quantity.setText(String.valueOf(quantityAmount));
                    calculateTotal();
                }
            }
        });

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityAmount>=0){
                    quantityAmount++;
                    quantity.setText(String.valueOf(quantityAmount));
                    calculateTotal();

                }
            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                if (name.matches("") || address.matches("") || phone.matches("")){
                    Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_LONG).show();

                }else{
                    String data = "Sending the order from "+ name;
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                    String order;
                    if (quantityAmount >1){
                        order = quantity.getText().toString() + " pieces of " + foodTitle.getText().toString();
                    }else{
                        order = quantity.getText().toString() + " piece of " + foodTitle.getText().toString();
                    }
                    String currentTime = getTime();
                    Order newOrder = new Order(name,address,phone,order,currentTime);
                    databaseReference.child("Orders").push().setValue(newOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                String data = "The order was send";
                                Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
                            }else {
                                String data = "An error occurred";
                                Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+currentVenue.getPhone()));
                startActivity(intent);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri map = Uri.parse("geo:0,0?q="+currentVenue.getName()+","+currentVenue.getAddress());
                Intent intent = new Intent(Intent.ACTION_VIEW, map);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"You do not have Google Maps app installed on your device",Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        Glide
                .with(getApplicationContext())
                .load(currentVenue.getImagePath())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        venueImage.setImageBitmap(resource);

                    }
                });

        venueName.setText(currentVenue.getName());
        venueCategory.setText(currentVenue.getCategory());
        venueAddress.setText(currentVenue.getAddress());
        venuePhone.setText(currentVenue.getPhone());

        calculateTotal();

        if (currentVenue.getCategory().equals(MainActivity.RESTAURANT)){
            detailsCard.setVisibility(View.GONE);
        }



    }

    public void calculateTotal(){
        totalPriceCalculation = Double.parseDouble(currentDish.getPrice()) * quantityAmount;
        calculatedTotalPrice.setText(MyAppAdapter.currencyFormat.format(totalPriceCalculation));
    }

    private String getTime(){
        String minutes = DateFormat.getDateTimeInstance().format(new Date());
        return minutes;
    }


}

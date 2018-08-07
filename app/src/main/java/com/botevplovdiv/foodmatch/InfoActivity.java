package com.botevplovdiv.foodmatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoActivity extends AppCompatActivity {

    ImageView foodImage;
    TextView foodTitle;
    TextView category;
    TextView description;
    TextView price;
    Button order;
    FoodDish current;
    Venue currentVenue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        foodImage = (ImageView) findViewById(R.id.foodImageInfoActivity);
        foodTitle = (TextView) findViewById(R.id.infoFoodTitle);
        category = (TextView) findViewById(R.id.infoFoodCategory);
        description = (TextView) findViewById(R.id.infoFoodDescription);
        price = (TextView) findViewById(R.id.infoItemPrice);
        order = (Button) findViewById(R.id.infoOrderButton);

        Bundle extras = getIntent().getExtras();

        current = extras.getParcelable("current");
        currentVenue = extras.getParcelable("currentVenue");

        Glide
                .with(getApplicationContext())
                .load(current.getImagePath())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        foodImage.setImageBitmap(resource);

                    }
                });
        foodTitle.setText(current.getTitle());
        category.setText(current.getCategory());
        description.setText(current.getDescription());
        price.setText(MyAppAdapter.currencyFormat.format(Double.parseDouble(current.getPrice())));

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putParcelable("currentDish",current);
                extras.putParcelable("currentVenue",currentVenue);
                Intent intent = new Intent(InfoActivity.this,OrderActivity.class).putExtras(extras);
                startActivity(intent);
            }
        });

    }

}

package com.botevplovdiv.foodmatch;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

public class LikedProduct extends AppCompatActivity {

    Button smallBackButton;
    Button viewSimilar;
    Button saveForLater;
    Button orderButton;
    String TAG = "FoodMatch";
    FoodDish current;
    Venue currentVenue;
    long venueId;
    ImageView imagePic;
    ImageView imagePicSmall;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_product);

        Bundle extras = getIntent().getExtras();

        current = extras.getParcelable("current");
        currentVenue = extras.getParcelable("currentVenue");
        imagePic = (ImageView) findViewById(R.id.likedImagePic);
        imagePicSmall = (ImageView) findViewById(R.id.likedImagePicSmall);

        int displayMetrics = getResources().getDisplayMetrics().densityDpi;

        if (displayMetrics <MainActivity.DENSITY_LIMIT){

            imagePic.setVisibility(View.GONE);

            Glide
                    .with(getApplicationContext())
                    .load(current.getImagePath())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            imagePicSmall.setImageBitmap(resource);

                        }
                    });

        }else{

            imagePicSmall.setVisibility(View.GONE);

            Glide
                    .with(getApplicationContext())
                    .load(current.getImagePath())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            imagePic.setImageBitmap(resource);

                        }
                    });
        }




        saveForLater = (Button) findViewById(R.id.save_for_later);
        saveForLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.likedProductsList.contains(current))
                    MainActivity.likedProductsList.add(current);
                finish();
            }
        });

        viewSimilar = (Button) findViewById(R.id.view_similar);
        viewSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String similarCategory = current.getCategory();
                Intent similarTag = new Intent().putExtra("simularTag",similarCategory);
                setResult(RESULT_OK,similarTag);
                finish();
            }
        });


        smallBackButton = (Button) findViewById(R.id.smallBackButton);
        smallBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        orderButton = (Button) findViewById(R.id.order);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putParcelable("currentDish",current);
                extras.putParcelable("currentVenue",currentVenue);
                Intent intent = new Intent(LikedProduct.this,OrderActivity.class).putExtras(extras);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"Entered onStart() method in LikedProduct");
    }
}

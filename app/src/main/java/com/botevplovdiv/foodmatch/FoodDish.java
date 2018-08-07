package com.botevplovdiv.foodmatch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stelio on 3/31/2017.
 */

public class FoodDish implements Parcelable{

    private String title;
    private String imagePath;
    private String description;
    private String price;
    private String category;
    private String venueType;
    private long venueId;

    public FoodDish(){
        //default constructor with no arguments required for calls to DataSnapshot.getValue()
        // method in Firebase
    }

    public FoodDish(String title,String imagePath,String description,String price, String category,String venueType,long venueId) {
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.price = price;
        this.category = category;
        this.venueType = venueType;
        this.venueId = venueId;
    }


    //getters
    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getVenueType() {
        return venueType;
    }

    public long getVenueId(){
        return this.venueId;
    }

    //setters

    public void setVenueId(long id){
        if (id > 0){
            this.venueId = id;
        }
    }

    //Parcel part

    public FoodDish(Parcel in ) {
        readFromParcel( in );
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<FoodDish> CREATOR
            = new Parcelable.Creator<FoodDish>() {
        public FoodDish createFromParcel(Parcel in) {
            return new FoodDish(in);
        }

      public FoodDish[] newArray(int size){
          return new FoodDish[size];
      }
    };


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(imagePath);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(category);
        dest.writeString(venueType);
        dest.writeLong(venueId);
    }

    private void readFromParcel(Parcel in ) {

        title = in.readString();
        imagePath = in .readString();
        description = in .readString();
        price   = in .readString();
        category = in.readString();
        venueType = in.readString();
        venueId = in.readLong();

    }

}


package com.botevplovdiv.foodmatch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stelio on 5.6.2017 г..
 */

public class Venue implements Parcelable{

    private String name;
    private String address;
    private String phone;
    private String category;
    private String imagePath;
    private long venueId;


    public Venue() {
    }

    public Venue(String name, String address, String phone, String category, String imagePath, long venueId) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.category = category;
        this.imagePath = imagePath;
        this.venueId = venueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getVenueId() {
        return venueId;
    }


    //Parcel part

    public Venue(Parcel in ) {
        readFromParcel( in );
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Venue> CREATOR
            = new Parcelable.Creator<Venue>() {
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        public Venue[] newArray(int size){
            return new Venue[size];
        }
    };


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(imagePath);
        dest.writeString(category);
        dest.writeLong(venueId);
    }

    private void readFromParcel(Parcel in ) {

        name = in.readString();
        address = in .readString();
        phone = in.readString();
        imagePath = in .readString();
        category   = in .readString();
        venueId = in.readLong();

    }
}

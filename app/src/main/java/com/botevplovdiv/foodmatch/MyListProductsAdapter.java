package com.botevplovdiv.foodmatch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Stelio on 29.5.2017 г..
 */

public class MyListProductsAdapter extends ArrayAdapter<FoodDish>  {

    List<FoodDish> productsList;
    HashMap<Long,Venue> venueList;

    private static class ViewHolder {
        ImageView foodImageView;
        TextView foodTitleTextView;
        TextView venueTextView;
        TextView priceTextView;
    }

    public MyListProductsAdapter(Context context, List<FoodDish> savedProductsList, HashMap<Long,Venue> venueList) {
        super(context, -1,savedProductsList);
        this.productsList = savedProductsList;
        this.venueList = venueList;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final FoodDish foodDish = getItem(position);
        final Venue currentVenue = venueList.get(foodDish.getVenueId());

        final ViewHolder viewHolder;

        if (convertView == null) { // no reusable ViewHolder, so create one
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView =
                    inflater.inflate(R.layout.saveditem, parent, false);
            viewHolder.foodImageView =
                    (ImageView) convertView.findViewById(R.id.foodImage);
            viewHolder.foodTitleTextView =
                    (TextView) convertView.findViewById(R.id.foodTitleText);
            viewHolder.venueTextView =
                    (TextView) convertView.findViewById(R.id.venueText);
            viewHolder.priceTextView =
                    (TextView) convertView.findViewById(R.id.priceText);
            convertView.setTag(viewHolder);
        }
        else { // reuse existing ViewHolder stored as the list item's tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide
                .with(getContext().getApplicationContext())
                .load(foodDish.getImagePath())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        viewHolder.foodImageView.setImageBitmap(resource);
                    }
                });

        viewHolder.foodTitleTextView.setText(foodDish.getTitle());
        viewHolder.venueTextView.setText(foodDish.getVenueType());
        viewHolder.priceTextView.setText(MyAppAdapter.currencyFormat.format(Double.parseDouble(foodDish.getPrice())));

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                productsList.remove(position);
                                MainActivity.likedProductsList.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putParcelable("currentDish",foodDish);
                extras.putParcelable("currentVenue",currentVenue);
                Intent intent = new Intent(getContext(),OrderActivity.class).putExtras(extras);
                v.getContext().startActivity(intent);

            }
        });

        return convertView;
    }
}

package com.botevplovdiv.foodmatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stelio on 13.5.2017 г..
 */



public class MyAppAdapter extends BaseAdapter {

    private static class ViewHolder {
        public FrameLayout background;
        public TextView priceTagText;
        public ImageView cardImage;
        public TextView shortDescriptionTextView;
        public TextView title;
        public TextView categoryText;
    }
    ;
    public List<FoodDish> dishesList = new ArrayList<>();
    public Context mContext;
    public static final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance();

    public MyAppAdapter(Context context, List<FoodDish> foodDishes) {
        this.dishesList = foodDishes;
        this.mContext = context;
    }



    @Override
    public int getCount() {
        return dishesList.size();
    }

    @Override
    public Object getItem(int position) {
        return dishesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }






    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        FoodDish current = dishesList.get(position);

        final ViewHolder viewHolder;


        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.cards_layout, parent,false);
            // configure view holder
            viewHolder = new ViewHolder();
            viewHolder.priceTagText = (TextView) convertView.findViewById(R.id.priceTagText);
            viewHolder.background = (FrameLayout) convertView.findViewById(R.id.background);
            viewHolder.cardImage = (ImageView) convertView.findViewById(R.id.cardImage);
            viewHolder.shortDescriptionTextView = (TextView) convertView.findViewById(R.id.shortDescription);
            viewHolder.title = (TextView) convertView.findViewById(R.id.titleText);
            viewHolder.categoryText = (TextView) convertView.findViewById(R.id.categoryText);



            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide
                .with(mContext.getApplicationContext())
                .load(current.getImagePath())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        viewHolder.cardImage.setImageBitmap(resource);
                    }
                });



        viewHolder.priceTagText.setText(currencyFormat.format(Double.parseDouble(current.getPrice())));
        viewHolder.title.setText(current.getTitle());
        viewHolder.shortDescriptionTextView.setText(current.getDescription());
        viewHolder.categoryText.setText(current.getCategory());



        return convertView;
    }


}

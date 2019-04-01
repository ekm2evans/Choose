package com.pickupgame.evans.choose.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.mattyork.colours.Colour;
import com.pickupgame.evans.choose.Activity.GameActivity;
import com.pickupgame.evans.choose.R;
import com.pickupgame.evans.choose.Utilities.var;

import java.util.ArrayList;
import java.util.Random;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    static Random randomGenerator;

    static {
        randomGenerator = new Random();
    }

    private ArrayList<CategoryList> mDataset;
    private int categoryLayout;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoryAdapter(ArrayList<CategoryList> myDataset, int layout) {
        mDataset = myDataset;
        categoryLayout = layout;
    }

    @Override
    public int getItemViewType(int position) {
//        boolean isNotPhoto = (mDataset.get(position).getPhotoUrl() == null);
//        if (mDataset.get(position).get_my_message()){
//            if (isNotPhoto){
//            return R.layout.msg_item_other;}
//            else {
//                //photo layout
//                return R.layout.msg_item_photo_me;
//            }
//        } else {
//            if (isNotPhoto){
//                return R.layout.category_item;}
//            else {
//                //photo layout
//                return R.layout.msg_item_photo;
//            }
//        }
        return R.layout.category_item;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        contrastTextColor(holder.category_text_view);
        holder.category_text_view.setText(mDataset.get(position).getCategory());
        //  bounce(holder.category_text_view);
        expand(holder.category_text_view);
        holder.category_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandextra(holder.category_text_view, position);
//                    startActivityWithCategory(holder.category_text_view.getContext(), mDataset.get(position).getCategory());
            }
        });

    }

    private void contrastTextColor(TextView v) {
        Drawable drawable = v.getContext().getDrawable(R.drawable.category_shape);
        // prepare
        int strokeWidth = 10; // 5px not dp
        int roundRadius = 15; // 15px not dp
        int strokeColor = Color.parseColor("#2E3135");
//        int fillColor = Color.parseColor("#DFDFE0");
        int fillColor = Color.parseColor(generateColor());
        int contrastColor = Colour.blackOrWhiteContrastingColor(fillColor);

        GradientDrawable gd = (GradientDrawable) drawable;
        gd.setColor(fillColor);


//        gd.setAlpha(100);
//        gd.setCornerRadius(roundRadius);
        gd.setStroke(strokeWidth, contrastColor);
//        int contrastingColor = ;
        v.setBackground(gd);
        v.setTextColor(contrastColor);
    }

    private String generateColor() {
        int newColor = randomGenerator.nextInt(0x1000000);
        return String.format("#%06X", newColor);
    }

    private void startActivityWithCategory(Context v, String category) {
        Intent myIntent = new Intent(v, GameActivity.class);
        myIntent.putExtra(var.CATEGORY, category); //Optional parameters

        v.startActivity(myIntent);
    }

    private void expandextra(TextView category_text_view, int position) {
        ScaleAnimation transAnim = new ScaleAnimation(1, 15f, 1, 15f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
//            transAnim.setDuration(500);
        transAnim.setDuration(300);
//            transAnim.setInterpolator(new Z);
        transAnim.setFillAfter(false);
        transAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivityWithCategory(category_text_view.getContext(), mDataset.get(position).getCategory());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        category_text_view.startAnimation(transAnim);
    }

    private void expand(TextView category_text_view) {
        ScaleAnimation transAnim = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
//            transAnim.setDuration(500);
        transAnim.setDuration(1000);
//            transAnim.setInterpolator(new Z);
        //  transAnim.setFillAfter(false);
        category_text_view.startAnimation(transAnim);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView category_text_view;
        public TextView user_text_view;
        ImageView photoImageView;

        public ViewHolder(View v) {
            super(v);
            category_text_view = v.findViewById(R.id.category_title);
        }
    }


}
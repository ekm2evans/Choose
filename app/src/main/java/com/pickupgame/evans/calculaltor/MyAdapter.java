package com.pickupgame.evans.calculaltor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<FriendlyMessage> mDataset;
    private int message_layout;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView message_text_view;
        public TextView user_text_view;
        ImageView photoImageView;
        public ViewHolder(View v) {
            super(v);
            message_text_view = v.findViewById(R.id.msg_view);
            user_text_view = v.findViewById(R.id.user_view);
            photoImageView = v.findViewById(R.id.imageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<FriendlyMessage> myDataset, int messagelayout) {
        mDataset = myDataset;
        message_layout = messagelayout;
    }

    @Override
    public int getItemViewType(int position) {
        boolean isNotPhoto = (mDataset.get(position).getPhotoUrl() == null);
        if (mDataset.get(position).get_my_message()){
            if (isNotPhoto){
            return R.layout.msg_item_other;}
            else {
                //photo layout
                return R.layout.msg_item_photo_me;
            }
        } else {
            if (isNotPhoto){
                return R.layout.msg_item;}
            else {
                //photo layout
                return R.layout.msg_item_photo;
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        boolean isNotPhoto = (mDataset.get(position).getPhotoUrl() == null);;
        if (isNotPhoto) {
            holder.message_text_view.setText(mDataset.get(position).getText());
        } else {
            Glide.with(holder.photoImageView.getContext())
                    .load(mDataset.get(position).getPhotoUrl())
                    .into(holder.photoImageView);
        }
        holder.user_text_view.setText(mDataset.get(position).getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
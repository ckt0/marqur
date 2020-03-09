package com.marqur.android;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarkerViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView contentView;

    public MarkerViewHolder(@NonNull View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.feed_item_title);
        contentView = itemView.findViewById(R.id.feed_item_content);
    }

    public void bind(Marker marker) {
        titleView.setText(marker.title);
        contentView.setText(marker.author);
    }
}

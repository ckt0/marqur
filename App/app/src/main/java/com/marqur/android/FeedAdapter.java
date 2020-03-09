package com.marqur.android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.jetbrains.annotations.NotNull;

public class FeedAdapter extends FirestoreRecyclerAdapter<Marker, FeedAdapter.MarkerFeedHolder> {
    private String[] mDataset;


    public FeedAdapter(@NonNull FirestoreRecyclerOptions<Marker> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MarkerFeedHolder holder, int position, @NonNull Marker model) {
        holder.textViewTitle.setText(model.title);
        holder.textViewContent.setText(model.author);
    }

    @NonNull
    @Override
    public MarkerFeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_list_item,
                parent, false);
        return new MarkerFeedHolder(v);
    }

    class MarkerFeedHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewContent;

        public MarkerFeedHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.feed_item_title);
            textViewContent = itemView.findViewById(R.id.feed_item_content);
        }
    }
}
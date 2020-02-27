package com.marqur.android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {

    public List<String> filenamelist;
    public List<String> filedonelist;

    public UploadListAdapter(List<String> filenamelist, List<String> filedonelist) {
        this.filenamelist = filenamelist;
        this.filedonelist = filedonelist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_upload_queue, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fileName = filenamelist.get(position);
        holder.fileNameView.setText(fileName);
        String fileDone = filedonelist.get(position);
        if (fileDone.equals("Uploading")) {
            holder.fileDoneView.setImageResource(R.drawable.progress);
        } else {
            holder.fileDoneView.setImageResource(R.drawable.done);
        }
    }

    @Override
    public int getItemCount() {
        return filenamelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fileNameView;
        public ImageView fileDoneView;
        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            fileNameView = (TextView) mView.findViewById(R.id.nameview);
            fileDoneView = (ImageView) mView.findViewById(R.id.upload);

        }
    }
}

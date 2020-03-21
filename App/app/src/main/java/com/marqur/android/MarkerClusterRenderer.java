package com.marqur.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class MarkerClusterRenderer extends DefaultClusterRenderer<MarkerCluster>

{   // 1

        private final IconGenerator iconGenerator;
        private final ImageView markerImageView;
        private final IconGenerator mClusterIconGenerator;
        private final ImageView mClusterImageView;
        private final int mDimension;
        private Context mContext;

        public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MarkerCluster> clusterManager) {
            super(context, map, clusterManager);
            mContext=context;
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View multiProfile = inflater.inflate(R.layout.multi_profile, null);
            mDimension = (int) context.getResources().getDimension(R.dimen.custom_profile_image);
            mClusterIconGenerator = new IconGenerator(context);
            mClusterIconGenerator.setContentView(multiProfile);

            iconGenerator = new IconGenerator(context);  // 3
            markerImageView = new ImageView(context);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
            int padding = (int) context.getResources().getDimension(R.dimen.custom_profile_padding);
            markerImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            markerImageView.setPadding(padding, padding, padding, padding);
            iconGenerator.setContentView(markerImageView);  // 4
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerCluster item, MarkerOptions markerOptions) { // 5
            if(item.markerPhoto==null)
                markerImageView.setImageResource(R.mipmap.marker);

            Bitmap icon = iconGenerator.makeIcon();  // 7
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));  // 8
            markerOptions.title(item.getTitle());
        }

    @Override
    protected void onClusterItemRendered(MarkerCluster clusterItem, Marker marker) {
            if(clusterItem.markerPhoto==null) {super.onClusterItemRendered(clusterItem,marker);}

            else {
                Glide.with(mContext.getApplicationContext())
                        .load(clusterItem.markerPhoto)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.1f)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                markerImageView.setImageDrawable(resource);
                                Bitmap icon = iconGenerator.makeIcon();
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }


                        });
            }
    }

    @Override
        protected void onClusterRendered(Cluster<MarkerCluster> cluster, Marker marker) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> MarkerPhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (MarkerCluster markerCluster : cluster.getItems()) {
                // Draw 4 at most.
                if (MarkerPhotos.size() == 4) break;
                try {
                    Glide.with(mContext.getApplicationContext())
                            .load(markerCluster.markerPhoto)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(new CustomTarget<Drawable>(){
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    resource.setBounds(0, 0, width, height);
                                    MarkerPhotos.add(resource);
                                    MultiDrawable multiDrawable = new MultiDrawable(MarkerPhotos);
                                    multiDrawable.setBounds(0, 0, width, height);

                                    mClusterImageView.setImageDrawable(multiDrawable);
                                    Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
    }




    @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
            }
}


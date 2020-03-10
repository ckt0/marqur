package com.marqur.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MarkerClusterRenderer extends DefaultClusterRenderer<MarkerCluster>

{   // 1
        private static final int MARKER_DIMENSION = 48;  // 2
        private final IconGenerator iconGenerator;
        private final ImageView markerImageView;
        public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MarkerCluster> clusterManager) {
            super(context, map, clusterManager);
            iconGenerator = new IconGenerator(context);  // 3
            markerImageView = new ImageView(context);
            markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
            iconGenerator.setContentView(markerImageView);  // 4
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerCluster item, MarkerOptions markerOptions) { // 5
            markerImageView.setImageResource(R.mipmap.marker);  // 6
            Bitmap icon = iconGenerator.makeIcon();  // 7
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));  // 8
            markerOptions.title(item.getTitle());
        }
    }


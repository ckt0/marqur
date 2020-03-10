package com.marqur.android;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerCluster implements ClusterItem {
    private String markername;
    private LatLng latLng;

    public MarkerCluster(String markername, LatLng latLng) {
        this.markername = markername;
        this.latLng = latLng;
    }

    @Override
    public LatLng getPosition() {  // The ClusterItem returns the position of the marker, which later Google Maps use’s and show the marker. Must always return same LatLng position.
        return latLng;
    }

    @Override
    public String getTitle() {  // Title of the marker which will be visible when you click on a single marker.
        return markername;
    }

    @Override
    public String getSnippet() {
        return "";
    }
}

package com.marqur.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

         // Inflate the layout for this fragment
         ViewGroup mapView = (ViewGroup) inflater.inflate(R.layout.activity_maps, container, false);

         return mapView;
     }

 }
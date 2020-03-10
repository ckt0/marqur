/**
 * Manipulates the map once available.
 * This callback is triggered when the map is ready to be used.
 * This is where we can add Marker or lines, add listeners or move the camera. In this case,
 * we just add a marker near Sydney, Australia.
 * If Google Play services is not installed on the device, the user will be prompted to install
 * it inside the SupportMapFragment. This method will only be triggered once the user has
 * installed Google Play services and returned to the app.
 */

package com.marqur.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int DEFAULT_ZOOM = 20;
    private static final String TAG = "Marqur Map Fragment";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private boolean isSwipeEnabled = false;
    private boolean count=true;
    private FloatingActionButton fab;
    private CardView cardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView
            (@NonNull LayoutInflater inflater,
             @Nullable ViewGroup container,
             @Nullable Bundle savedInstanceState) {

        View mapView = inflater.inflate(R.layout.page_map, null, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        return mapView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


//        requireView().findViewById(R.id.map).setOnClickListener(this);

        //initialise fused location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //Initialise FAB
        fab = getView().findViewById(R.id.search_fab);
        cardView=getView().findViewById(R.id.roam_actions);

        // Initialize the SDK
        Places.initialize(getActivity(), getString(R.string.google_maps_key));
        // Create a new Places client instance

        // Initialize the AutocompleteSupportFragment.
        // Use fields to define the data types to return.
//
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        //Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Collections.singletonList(Place.Field.LAT_LNG));
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NotNull Place place) {
//
//
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));
//                // TODO: Get info about the selected place.
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });

        //Floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng mapcoord;
                mapcoord = mMap.getCameraPosition().target;

                startActivity(new Intent(getActivity().getApplicationContext(), AddMarker.class).putExtra("latitude", mapcoord.latitude).putExtra("longitude", mapcoord.longitude));
            }
        });

    }






    public static MapFragment newInstance() {
        MapFragment mapFragment = new MapFragment();
        Bundle args = new Bundle();
        mapFragment.setArguments(args);
        return mapFragment;
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(isSwipeEnabled);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
                //TODO create a new activity to display the details of marker
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(count) {
                    isSwipeEnabled = true;
                    count=false;
                    cardView.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                }
                else{
                    isSwipeEnabled=false;
                    count=true;
                    cardView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                }
                mMap.getUiSettings().setScrollGesturesEnabled(isSwipeEnabled);
            }
        });

        //removing the POI and Transits
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            getLocationPermission();
            getDeviceLocation();
        }


        //TODO display markers that are around the current location


    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NotNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastKnownLocation = task.getResult();
                            // Set the map's camera position to the current location of the device.

                            mMap.clear();

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                }
            }
        }

    }

}

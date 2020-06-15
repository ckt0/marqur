package com.marqur.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class ReaderFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private RecyclerView feedRecyclerView;
    private FeedAdapter feedAdapter;
    private String[] myDataset = {"Test1","Test2","Test3","Test4",
            "Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4",
            "Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4",
            "Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4",};




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_reader, null);
        return root;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupView();
    }



    private void setupView() {

        //initialise fused location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Query used to build the Feed from
        Query query = ((MainActivity)requireActivity()).markersCRef.orderBy("date_created", Query.Direction.DESCENDING);

        // Using query and the Marker class to build the options needed by Feed Adapter
        FirestoreRecyclerOptions<Marker> options = new FirestoreRecyclerOptions.Builder<Marker>()
                .setQuery(query, Marker.class)
                .build();

        // Fetch RecyclerView
        feedRecyclerView = (RecyclerView) requireView().findViewById(R.id.feed_recycler_view);

        // Use a linear layout manager
        feedRecyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));

        // Increases performance. Use only if content does not change RecyclerView's layout size
        feedRecyclerView.setHasFixedSize(true);

        // Create a new Feed Adapter and attach it to Feed RecyclerView
        feedAdapter = new FeedAdapter(options);
        feedRecyclerView.setAdapter(feedAdapter);

        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            getLocationPermission();
            getDeviceLocation();
        }

        FloatingActionButton fab = getView().findViewById(R.id.reader_add_fab);
        fab.setOnClickListener( view1 -> {
            LatLng mapcoord = (mLastKnownLocation != null) ? new LatLng(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude()) : mDefaultLocation;

            startActivity(new Intent( requireActivity().getApplicationContext(), AddMarker.class).putExtra("latitude", mapcoord.latitude).putExtra("longitude", mapcoord.longitude));
        } );
    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener( requireActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete( Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastKnownLocation = task.getResult();

                        } else {
                            Log.d("READER", "Current location is null. Using defaults.");
                            Log.e("READER", "Exception: %s", task.getException());
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
        if (ContextCompat.checkSelfPermission( requireActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }



    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                getDeviceLocation();
            }
        }

    }



    @Override
    public void onStart() {
        super.onStart();
        feedAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedAdapter.stopListening();
    }

    public static ReaderFragment newInstance() {
        ReaderFragment FeedFragment = new ReaderFragment();
        Bundle args = new Bundle();
        FeedFragment.setArguments(args);
        return FeedFragment;
    }

}

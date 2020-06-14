/**
 * Manipulates the map once available.
 * This callback is triggered when the ma';/p is ready to be used.
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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
    private TextView mplace_name;
    private CardView cardView;
    private LatLng mapcoord;
    private Map<String, com.marqur.android.Marker> currently_fetched=new HashMap<>(  );
    private FirebaseFirestore firestore ;
    private ClusterManager<MarkerCluster> clusterManager;
    private MarkerCluster markers;







    @Override
    public void onCreate(Bundle savedInstanceState) {
        requireActivity().setTheme( R.style.Marqur_NoActionBar );
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
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);

        //Initialise firestore
        firestore = FirebaseFirestore.getInstance();



        mapFragment.getMapAsync(this);

        return mapView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //initialise fused location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        //Initialise FAB and Widgets
        fab = getView().findViewById(R.id.search_fab);
        cardView=getView().findViewById(R.id.roam_actions);
        mplace_name=getView().findViewById(R.id.place_name);
        // Initialize the SDK
        Places.initialize(requireActivity(), getString(R.string.google_maps_key));


        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        //Specify the types of place data to return.
        autocompleteFragment.setPlaceFields( Collections.singletonList(Place.Field.LAT_LNG));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected( Place place) {


                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));
                fetch_markers();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //Floating action button
        fab.setOnClickListener( view1 -> {

            mapcoord = mMap.getCameraPosition().target;

            startActivity(new Intent( requireActivity().getApplicationContext(), AddMarker.class).putExtra("latitude", mapcoord.latitude).putExtra("longitude", mapcoord.longitude));
        } );

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
        clusterManager = new ClusterManager<MarkerCluster>( requireActivity().getApplicationContext(), googleMap);
        clusterManager.setRenderer(new MarkerClusterRenderer(getActivity(), mMap, clusterManager));

        mMap.getUiSettings().setScrollGesturesEnabled(isSwipeEnabled);

        mMap.setOnMarkerClickListener( clusterManager );
        //Extracts the marker id from the snippet in order to identify the marker in the database
        clusterManager.setOnClusterItemClickListener( new ClusterManager.OnClusterItemClickListener<MarkerCluster>() {
            @Override
            public boolean onClusterItemClick(MarkerCluster clusterItem) {
                if(!clusterItem.getTitle().equals( "Current position" )) {
                    com.marqur.android.Marker marker1 = new com.marqur.android.Marker();
                    Iterator keyIterator = currently_fetched.keySet().iterator();
                    String id = clusterItem.getSnippet().substring( clusterItem.getSnippet().indexOf( "~" ) + 1 );
                    while (keyIterator.hasNext()) {
                        String key = keyIterator.next().toString();
                        if (id.equals( key )) {
                            marker1 = currently_fetched.get( key );
                            break;
                        }
                    }
                    Log.d( TAG, Objects.requireNonNull( marker1 ).geohash );
                   startActivity(new Intent( requireActivity().getApplicationContext(), Post.class).putExtra( "mar_details",marker1.mContent.text).putExtra( "picurl",marker1.mContent.getMedia().get( 0 ).getMedia_id() ));
                }

                return false;
            }
        } );
        //Set to enable or disable swipe when using maps
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
                    fab.setVisibility(View.VISIBLE);
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
                            requireContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            getLocationPermission();
            getDeviceLocation();
        }
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                fetch_markers();
            }
        });
    }

    private void fetch_markers() {
        LatLngBounds curScreen = mMap.getProjection()
                .getVisibleRegion().latLngBounds;
        String top_right=GeoHash.encodeHash(new LatLong(curScreen.northeast.latitude,curScreen.northeast.longitude));
        String bottom_left=GeoHash.encodeHash(new LatLong(curScreen.southwest.latitude,curScreen.southwest.longitude));
        firestore.collection("markers").whereGreaterThanOrEqualTo("geohash",bottom_left).whereLessThanOrEqualTo("geohash",top_right).orderBy( "geohash" ).orderBy( "upvotes" ).limit( 100 ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull( task.getResult() )) {
                        Log.d( TAG, document.getId() + " => " + document.getData() );

                        com.marqur.android.Marker marker = document.toObject( com.marqur.android.Marker.class );
                        if (!currently_fetched.containsKey( marker.markerid )) {
                            if (marker.mContent.media == null) {
                                markers = new MarkerCluster( marker.getTitle(), marker.getmContent().text + "~" + document.getId(), new LatLng( marker.getLocation().getLatitude(), marker.getLocation().getLongitude() ), null );
                            } else
                                markers = new MarkerCluster( marker.getTitle(), marker.getmContent().text + "~" + document.getId(), new LatLng( marker.getLocation().getLatitude(), marker.getLocation().getLongitude() ), marker.getmContent().getMedia().get( 0 ).media_id );
                            clusterManager.addItem( markers );
                            currently_fetched.put( marker.markerid,marker );

                        }
                    }

                    clusterManager.cluster();  // 5
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

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
                            // Set the map's camera position to the current location of the device.
                            LatLng position=new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude());
                            mMap.clear();

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    position, DEFAULT_ZOOM));
                            //Set the current location marker
                            mMap.addMarker( new MarkerOptions().position(position ).title( "Current position" ).icon( BitmapDescriptorFactory.fromResource(R.drawable.placeholder) ) );
                            getAddress();


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

    //Get details about the current location from lattitude and longitude
    private void getAddress() {
        String addressStr = "";
        Geocoder myLocation = new Geocoder(getContext(), Locale.getDefault());
        List<Address> myList = null;
        try {
            myList = myLocation.getFromLocation(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = null;
        if ((myList != null) && (myList.size() > 0)) {
            address = (Address) myList.get(0);
        }

        if (address != null) {
            addressStr += address.getAddressLine(0) ;
        }
        mplace_name.setText( addressStr);
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



}

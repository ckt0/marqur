package com.marqur.android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;
import com.google.firebase.firestore.GeoPoint;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.UUID;

import static android.content.Context.SENSOR_SERVICE;


/**
 * Controls the Lens Fragment used for AR features
 */
public class LensFragment extends Fragment {

    private Camera mCamera;
    private LensCameraPreview mPreview;
    private LinearLayout mPreviewHolder;
    private Button ar_button1, ar_button2;
    private boolean cameraFront = false;
    private boolean cameraPreviewOn = false;

    private float[] accelerometerReading = new float[3];
    private float[] magnetometerReading = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];
    private int bearing = 0;

    boolean[] markerVisible;
    Marker[] markerBuffer;

    private static final int PERMISSION_REQUEST_CODE = 200;



    /**
     * Runs while fragment view is being created. Inflates fragment.
     * @param inflater - Thing that draws layout views
     * @param container - The parent view in which new layout is inflated (drawn)
     * @param savedInstanceState - Saved instance data
     * @return The newly inflated view
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_lens, container, false);
    }



    /**
     * Runs after fragment view is loaded
     * @param view - Created view
     * @param savedInstanceState - Saved instance data
     */
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {

        markerVisible = new boolean[30];
        markerBuffer = new Marker[30];

        for (int i = 0; i < 30; i++){
            markerVisible[i] = false;
        }

        markerBuffer[0] = new Marker(UUID.randomUUID().toString(),"Dummy Marker #1","chris", new GeoPoint(37.422,-122.084),
                GeoHash.encodeHash(new LatLong(37.422,-122.084)),
                "16-03-2020","16-03-2020",0,0,0,0,0,
                new Content("Dummy Content","Dummy description", null));

        markerBuffer[1] = new Marker(UUID.randomUUID().toString(),"Dummy Marker #2","chris", new GeoPoint(37.421,-122.083),
                GeoHash.encodeHash(new LatLong(37.421,-122.083)),
                "16-03-2020","16-03-2020",0,0,0,0,0,
                new Content("Dummy Content","Dummy description", null));

        markerBuffer[2] = new Marker(UUID.randomUUID().toString(),"Dummy Marker #3","chris", new GeoPoint(37.420,-122.082),
                GeoHash.encodeHash(new LatLong(37.420,-122.082)),
                "16-03-2020","16-03-2020",0,0,0,0,0,
                new Content("Dummy Content","Dummy description", null));

        markerBuffer[3] = new Marker(UUID.randomUUID().toString(),"Dummy Marker #4","chris", new GeoPoint(37.419,-122.081),
                GeoHash.encodeHash(new LatLong(37.419,-122.081)),
                "16-03-2020","16-03-2020",0,0,0,0,0,
                new Content("Dummy Content","Dummy description", null));

        // Apply cool fade-blink animation to camera icon in the view
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(700);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        view.findViewById(R.id.camera_icon).startAnimation(animation);
    }


    public void initialiseCamera(){
        initialiseCamera(requireContext());
    }


    /**
     * Loads camera and attaches camera preview to the fragment
     */
    public void initialiseCamera(Context context) {

        // Keeps screen On
        ((MainActivity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // If camera permissions not granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Only request if camera page open
            if (((MainActivity)context).getPage() == 0) {
                ((MainActivity)context).setPage(1);
                ((MainActivity)context).requestPermission(Manifest.permission.CAMERA);
            }

        } else if (((MainActivity)context).getPage() == 0) {

            // Start camera in portrait mode
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);

            // Start and attach camera preview to Lens fragment
            mPreviewHolder = (LinearLayout) ((MainActivity)context).findViewById(R.id.lens_preview);
            mPreview = new LensCameraPreview((MainActivity)context, mCamera);

            Log.println(Log.ASSERT,"LENSFRAGMENT.IN","cameraPreviewOn = "+cameraPreviewOn);

//            if (cameraPreviewOn) {
                mPreviewHolder.addView(mPreview,0);
                cameraPreviewOn = true;
//            }

            Log.println(Log.ASSERT,"LENSFRAGMENT.IN","--> cameraPreviewOn = "+cameraPreviewOn);
//
//            if (cameraPreview.findViewById(R.id.loader)!=null)
//                cameraPreview.removeView(cameraPreview.findViewById(R.id.loader));
            mCamera.startPreview();

            //debug
            ar_button1 = (Button) ((MainActivity)context).findViewById(R.id.lens_button1);
            ar_button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            //debug
            ar_button2 = (Button)((MainActivity)context).findViewById(R.id.lens_button2);
            ar_button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            Log.println(Log.ASSERT,"LENSFRAGMENT","INITIALIZED CAMERA");
            startSensors(context);
        }
    }


    /**
     * Runs after fragment is loaded completely
     * @param savedInstanceState - Saved instance data
     */
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.println(Log.ASSERT,"LENSFRAGMENT","ACTIVITY CREATED");
        // Starts Camera
        if(((MainActivity)requireActivity()).getPage()==0) initialiseCamera(requireActivity());


    }


    /**
     * If fragment is paused, release camera
     */
    @Override
    public void onPause() {
        super.onPause();
        onPause(requireContext());
    }


    public void onPause(Context context){

//        mPreviewHolder = (LinearLayout) ((MainActivity)context).findViewById(R.id.lens_preview);
//        if (cameraPreview.findViewById(R.id.loader)!=null)
//            cameraPreview.removeView(cameraPreview.findViewById(R.id.loader));
//        cameraPreview.addView(cameraPreview.findViewById(R.id.loader));

//        Log.d("CAMERAPREVIEW:",""+cameraPreviewOn);

        Log.println(Log.ASSERT,"LENSFRAGMENT.PA","cameraPreviewOn = "+cameraPreviewOn);
            cameraPreviewOn = false;
        Log.println(Log.ASSERT,"LENSFRAGMENT.PA","--> cameraPreviewOn = "+cameraPreviewOn);
//
//        if (cameraPreview.findViewById(R.id.loader)!=null)
//            cameraPreview.removeView(cameraPreview.findViewById(R.id.loader));

        // Release camera and screen
        ((MainActivity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        releaseCamera();
//        mPreviewHolder.removeView(mPreview);

        Log.println(Log.ASSERT,"LENSFRAGMENT","PAUSED");
    }


    /**
     * When fragment resumes, re-open camera
     */
    public void onResume() {
        super.onResume();
        Log.println(Log.ASSERT,"LENSFRAGMENT","RESUMED");

    }



    /**
     * Stop and release camera
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }







    /**
     * Creates and returns the Lens fragment
     * @return the Lens Fragment
     */
    static LensFragment newInstance() {
        LensFragment lensFragment = new LensFragment();
        Bundle args = new Bundle();
        lensFragment.setArguments(args);
        Log.println(Log.ASSERT,"LENSFRAGMENT","NEW INSTANCE CREATED");
        return lensFragment;
    }


    /**
     * Finds id of front-facing camera
     * @return id of front-facing camera
     */
    private int findFrontFacingCamera() {

        int cameraId = -1;

        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();

        //for every camera, check whether front-facing
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }

        //finally, return front-facing camera id
        return cameraId;

    }


    /**
     * Finds id of back-facing camera
     * @return id of back-facing camera
     */
    private int findBackFacingCamera() {

        int cameraId = -1;

        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();

        //for every camera, check whether back-facing
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }

        //finally, return back-facing camera id
        return cameraId;
    }


    /**
     * Toggles active camera - front || back
     */
    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {

                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {

                //open the frontFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPreview.refreshCamera(mCamera);
            }
        }
    }



    public void startSensors(){
        startSensors(getContext());
    }



    /**
     * Gets device sensors and attaches listeners to them
     */
    public void startSensors(Context context) {

        // Gets device sensor manager
        SensorManager sensorManager = (SensorManager) ((MainActivity)context).getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;   // asserts are useless but they keep Android Studio happy.

        // Gets device accelerometer
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // If accelerometer obtained successfully, register the accelListener defined below
        if (accelerometer != null) {
            sensorManager.registerListener(new accelListener(),
                    accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Log.println(Log.WARN,"Lens: ","No Accelerometer Available!");
        }

        // Gets device magnetic field sensor (magnetometer)
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // If magnetometer obtained successfully, register the magnetListener defined below
        if (magneticField != null) {
            sensorManager.registerListener(new magnetListener(),
                    magneticField, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Log.println(Log.WARN,"Lens: ","No Magnetometer Available!");
        }
    }



    /**
     * Accelerometer Listener that updates accelerometerReading variable
     */
    private class accelListener implements SensorEventListener {


        /**
         * Runs when sensor updates
         * @param sensorEvent Holds readings from sensor
         */
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            // Copies sensor readings to variable accelerometerReading
            System.arraycopy(sensorEvent.values, 0, accelerometerReading,
                    0, accelerometerReading.length);

            // Derives orientation from magnetometer and accelerometer readings
            updateOrientationAngles();

            //debug
//            if(orientationAngles[1]<-0.9) {
//                ar_button1.setText("Valid");
//            } else {
//                ar_button1.setText("Invalid");
//            }
        }


        /**
         * Runs when sensor accuracy updates
         * @param sensor id of sensor
         * @param i new accuracy of sensor
         */
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}

    }

    /**
     * Magnetic Field (Compass) Listener that updates accelerometerReading variable
     */
    private class magnetListener implements SensorEventListener {


        /**
         * Runs when sensor updates
         * @param sensorEvent Holds readings from sensor
         */
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            // Copies sensor readings to variable magnetometerReading
            System.arraycopy(sensorEvent.values, 0, magnetometerReading,
                    0, magnetometerReading.length);

            // Derives devices orientation from magnetometer and accelerometer readings
            updateOrientationAngles();

            // Converts the orientation angle to compass degrees.
            bearing = (int) Math.round(Math.toDegrees(orientationAngles[0]));

            //debug
//            ar_button2.setText(String.format(Locale.getDefault(),"%d", bearing));

            drawMarkers();
        }


        /**
         * Runs when sensor accuracy updates
         * @param sensor id of sensor
         * @param i new accuracy of sensor
         */
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}
    }


    /**
     * Compute the three orientation angles based on the most recent readings from
     * the device's accelerometer and magnetometer.
     */
    public void updateOrientationAngles() {

        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // Update orientation angles using rotation matrix.
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
    }



    public void drawMarkers(){

        Location myLoc = ((MainActivity)requireActivity()).getMyLocation();
        if (myLoc==null) return;

        ViewGroup root = requireView().findViewById(R.id.lens_ar_canvas);

        double inclination = Math.round(Math.toDegrees(Math.acos(rotationMatrix[8])));
        ar_button1.setText(String.format(Locale.getDefault(), "%.2f", inclination));

        if(inclination > 90) {
            if (bearing >= 0) bearing -= 180;
            else bearing += 180;
        }
        ar_button2.setText(String.format(Locale.getDefault(),"%d", bearing));

        for(int i = 0; i<30; i++) {

            if (markerBuffer[i]!=null) {

                Location markerLoc = new Location("");
                markerLoc.setLatitude(markerBuffer[i].location.getLatitude());
                markerLoc.setLongitude(markerBuffer[i].location.getLongitude());

                double distance = myLoc.distanceTo(markerLoc);

                double x = Math.cos(markerLoc.getLatitude());
                x = x * Math.sin(markerLoc.getLongitude() - myLoc.getLongitude());

                double y = Math.cos(myLoc.getLatitude()) * Math.sin(markerLoc.getLatitude());
                y = y - Math.sin(myLoc.getLatitude()) * Math.cos(markerLoc.getLatitude());
                y = y * Math.cos(markerLoc.getLongitude() - myLoc.getLongitude());

                double markerHeading = Math.toDegrees(Math.atan2(x, y));


                if ((distance < 500) && (bearing < (markerHeading + 30)) && (bearing > (markerHeading - 30))) {
                   //if (inclination>30)
                    if (!markerVisible[i]) {

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300,300);

                        View markerView = LayoutInflater.from(requireContext()).inflate(R.layout.lens_marker, root, false);
                        ((TextView) markerView.findViewById(R.id.lens_marker_view_title)).setText(markerBuffer[i].title);
                        markerView.setId(getMarkerViewId(i));
                        root.addView(markerView,params);

//                        LensMarkerView marker = new LensMarkerView(requireContext());
//                        marker.init(markerBuffer[i].title);
//                        marker.setId(getMarkerViewId(i));
//                        root.addView(marker,params);

                        Log.println(Log.ASSERT,"LENSFRAGMENT.D","ADDED MARKER #"+i);
                        markerVisible[i] = true;
//                        ar_button1.setText("MATCH!");
                    }

                    View markerView = root.findViewById(getMarkerViewId(i));

                    float markerViewCentreX = (float) (markerView.getWidth() / 2.0);
                    float markerViewCentreY = (float) (markerView.getHeight() / 2.0);
                    float rootCentreX = (float) (root.getX() + root.getWidth() / 2.0);
                    float markerCentrePositionX = (float) ((rootCentreX - root.getX())-markerViewCentreX);

                    if(inclination > 90)
                        markerView.setTranslationY((float)
                                (((-(-1.6 - orientationAngles[1]) / 1.6) * root.getHeight()) +
                                        (root.getHeight() - ((distance / 500) * root.getHeight()) - markerViewCentreY)));
                    else
                        markerView.setTranslationY((float)
                                ((((-1.6 - orientationAngles[1]) / 1.6) * root.getHeight()) +
                                        (root.getHeight() - ((distance / 500) * root.getHeight()) - markerViewCentreY)));

                    markerView.setTranslationX((float)
                            (markerCentrePositionX - (bearing - markerHeading) * (root.getWidth() / 60.0)));
//                    ar_button1.setText(String.format(Locale.getDefault(), "%.2f", distance));

                } else {


                    if (markerVisible[i]) {
                        Log.println(Log.ASSERT,"LENSFRAGMENT.D","REMOVING MARKER #"+i);
//                        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
//                        anim.setDuration(200);
//                        anim.setRepeatCount(NUM_REPEATS);
//                        anim.setRepeatMode(Animation.REVERSE);
//                        root.findViewById(getMarkerViewId(i)).startAnimation(anim);
                        root.removeView(root.findViewById(getMarkerViewId(i)));
                        markerVisible[i] = false;
//                        ar_button1.setText(String.format(Locale.getDefault(), "%.2f", markerHeading));
                    }
                }
            }
        }
    }

    private int getMarkerViewId(int i) {
        switch(i){
            case 0: return R.id.lens_marker_0;
            case 1: return R.id.lens_marker_1;
            case 2: return R.id.lens_marker_2;
            case 3: return R.id.lens_marker_3;
            case 4: return R.id.lens_marker_4;
            case 5: return R.id.lens_marker_5;
            case 6: return R.id.lens_marker_6;
            case 7: return R.id.lens_marker_7;
            case 8: return R.id.lens_marker_8;
            case 9: return R.id.lens_marker_9;
            case 10: return R.id.lens_marker_10;
            case 11: return R.id.lens_marker_11;
            case 12: return R.id.lens_marker_12;
            case 13: return R.id.lens_marker_13;
            case 14: return R.id.lens_marker_14;
            case 15: return R.id.lens_marker_15;
            case 16: return R.id.lens_marker_16;
            case 17: return R.id.lens_marker_17;
            case 18: return R.id.lens_marker_18;
            case 19: return R.id.lens_marker_19;
            case 20: return R.id.lens_marker_20;
            case 21: return R.id.lens_marker_21;
            case 22: return R.id.lens_marker_22;
            case 23: return R.id.lens_marker_23;
            case 24: return R.id.lens_marker_24;
            case 25: return R.id.lens_marker_25;
            case 26: return R.id.lens_marker_26;
            case 27: return R.id.lens_marker_27;
            case 28: return R.id.lens_marker_28;
            default: return R.id.lens_marker_29;
        }
    }
}
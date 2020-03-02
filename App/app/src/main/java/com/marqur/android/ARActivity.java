package com.marqur.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import androidx.fragment.app.Fragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import static android.content.Context.SENSOR_SERVICE;

public class ARActivity extends Fragment {

    private Camera mCamera;
    private ARCameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Button ar_button1, ar_button2;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    public static Bitmap bitmap;

    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private int direction = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_ar, null);
        return root;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        // you can add listener of elements here
          /*Button mButton = (Button) view.findViewById(R.id.button);
            mButton.setOnClickListener(this); */
//        mTextView = view.findViewById(R.id.mTextView);
//        mLinearLayout = (LinearLayout)view;

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = getActivity();

        mCamera =  Camera.open();
        mCamera.setDisplayOrientation(90);
        cameraPreview = (LinearLayout) getView().findViewById(R.id.cPreview);
        mPreview = new ARCameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        ar_button1 = (Button) getView().findViewById(R.id.gyroVal);
        ar_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ar_button2 = (Button) getView().findViewById(R.id.compVal);
        ar_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //get the number of cameras
//                int camerasNumber = Camera.getNumberOfCameras();
//                if (camerasNumber > 1) {
//                    //release the old camera instance
//                    //switch camera, from the front and the back and vice versa
//
//                    releaseCamera();
//                    chooseCamera();
//                } else {
//
//                }
            }
        });

        mCamera.startPreview();

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startSensors();
    }

    public static ARActivity newInstance() {
        ARActivity ARFragment = new ARActivity();
        Bundle args = new Bundle();
        ARFragment.setArguments(args);
        return ARFragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_ar);
//
//    }

    private int findFrontFacingCamera() {

        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;

    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;

            }

        }
        return cameraId;
    }

    public void onResume() {

        super.onResume();
        if(mCamera == null) {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
            Log.d("nu", "null");
        }else {
            Log.d("nu","no null");
        }

    }

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
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Intent intent = new Intent(getActivity(),PictureActivity.class);
                startActivity(intent);
            }
        };
        return picture;
    }



    public void startSensors() {

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener accelerometerSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                System.arraycopy(sensorEvent.values, 0, accelerometerReading,
                        0, accelerometerReading.length);
                updateOrientationAngles();
                if(mOrientationAngles[1]<-0.9) {
                    ar_button1.setText("Valid");
                } else {
                    ar_button1.setText("Invalid");
                }
//                ar_button1.setText(String.format("%f",sensorEvent.values[2]));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        if (accelerometer != null) {
            sensorManager.registerListener(accelerometerSensorListener,
                    accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        SensorEventListener magneticSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                System.arraycopy(sensorEvent.values, 0, magnetometerReading,
                        0, magnetometerReading.length);
                updateOrientationAngles();
                ar_button2.setText(String.format("%d",direction));
//                ar_button2.setText(String.format("%f",mOrientationAngles[1]));
//                ar_button2.setText(String.format("%f",sensorEvent.values[2]));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        if (magneticField != null) {
            sensorManager.registerListener(magneticSensorListener,
                    magneticField, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {

        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);
        // "rotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, mOrientationAngles);
        // "mOrientationAngles" now has up-to-date information.

        direction = (int) Math.round(Math.toDegrees(mOrientationAngles[0]));

    }

}
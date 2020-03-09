package com.marqur.android;

import java.io.IOException;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

/**
 * The camera preview used by Lens
 */
public class LensCameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;


    /**
     * Constructs the camera preview
     * @param context - Activity calling the preview, passed as is Lens fragment
     * @param camera - Camera object interface to the device camera
     */
    public LensCameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    /**
     * Runs when surface view created
     * @param holder - The parent view holding the surface view
     */
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // Attach camera preview and start camera
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();

                Log.println(Log.ASSERT,"LENSCAMERAPREVIEW","SURFACE CREATED");
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    /**
     * Runs when surface view undergoes changes
     * @param holder - parent view holding the surface
     * @param format - PixeFormat of the surface
     * @param w - surface width
     * @param h - surface height
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Make sure to stop the preview before resizing or reformatting it.
        // Refreshes camera preview

        Log.println(Log.ASSERT,"LENSCAMERAPREVIEW","SURFACE CHANGED");
        refreshCamera(mCamera);
    }

    /**
     * Refresh the camera preview
     * @param camera - The camera object
     */
    public void refreshCamera(Camera camera) {

        // if preview surface does not exist, return
        if (mHolder.getSurface() == null) {
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        setCamera(camera);
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


    /**
     * Set (new) camera object
     * @param camera - The camera object
     */
    public void setCamera(Camera camera) {
        mCamera = camera;
    }


    /**
     * Run when surface is destroyed
     * @param holder - Parent view holding surface
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.println(Log.ASSERT,"LENSCAMERAPREVIEW","SURFACE DESTROYED");
//        mCamera.release();
    }
}
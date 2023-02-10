package com.arcsoft.v4l2cameradisplay;

import android.util.Log;

import com.arcsoft.v4l2cameradisplay.gles.DisplaySurfaceView;


public class ProcessThread extends Thread {
    private final static String TAG = "ProcessThread";
    private DisplaySurfaceView mDisplaySurfaceView;
    private int mWidth, mHeight;
    public ProcessThread(DisplaySurfaceView displaySurfaceView) {
        mDisplaySurfaceView = displaySurfaceView;
    }
    public boolean init() {
        int ret = init_camera();
        if (ret != 0) {
            Log.e(TAG, "init camera error code:" + ret);
            return false;
        }
        mWidth = get_width();
        mHeight = get_height();
        Log.d(TAG, "camera width = " + mWidth);
        Log.d(TAG, "camera height = " + mHeight);
        return true;
    }
    @Override
    public void run() {
        while (true) {
            mDisplaySurfaceView.mGlRenderer.nv12 = get_nv12_frame();
            mDisplaySurfaceView.mGlRenderer.mWidth = mWidth;
            mDisplaySurfaceView.mGlRenderer.mHeight = mHeight;
            if (mDisplaySurfaceView.mGlRenderer.nv12.length > 0) {
                Log.d(TAG, "get frame len = " + mDisplaySurfaceView.mGlRenderer.nv12.length);
                mDisplaySurfaceView.requestRender();
            }
        }
    }
    public native int init_camera();
    public native int get_width();
    public native int get_height();
    public native byte[] get_nv12_frame();
}

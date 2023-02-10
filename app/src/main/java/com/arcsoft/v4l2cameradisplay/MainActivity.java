package com.arcsoft.v4l2cameradisplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.arcsoft.v4l2cameradisplay.databinding.ActivityMainBinding;
import com.arcsoft.v4l2cameradisplay.gles.DisplaySurfaceView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'v4l2cameradisplay' library on application startup.
    static {
        System.loadLibrary("v4l2cameradisplay");
    }

    private ActivityMainBinding binding;
    private DisplaySurfaceView mDisplaySurfaceView;
    private ProcessThread mProcessThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mDisplaySurfaceView = findViewById(R.id.surfaceView);
        mProcessThread = new ProcessThread(mDisplaySurfaceView);
        if (true == mProcessThread.init()) {
            mProcessThread.start();
        }
    }
}
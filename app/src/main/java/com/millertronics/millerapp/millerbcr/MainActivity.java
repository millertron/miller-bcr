package com.millertronics.millerapp.millerbcr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String initText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "  OpenCVLoader.initDebug(), not working.");
            initText = "OpenCV not loaded...";
        } else {
            Log.d(TAG, "  OpenCVLoader.initDebug(), working.");
            initText = "OpenCV loaded!!";
        }

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(initText);
    }
}

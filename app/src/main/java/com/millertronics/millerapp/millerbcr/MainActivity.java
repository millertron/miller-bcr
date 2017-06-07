package com.millertronics.millerapp.millerbcr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String initText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);
        if (getIntent() != null && StringUtils.isNotBlank(getIntent().getStringExtra(CameraReaderActivity.TEXT_DATA_KEY))){
            initText = getIntent().getStringExtra(CameraReaderActivity.TEXT_DATA_KEY);
        } else {
            initText = "No text";
        }
        textView.setText(initText);
    }
}

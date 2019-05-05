package com.sevencrayons.compass;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class CompassActivity extends AppCompatActivity {

    private static final String TAG = "CompassActivity";

    private Compass compass;
    private SOTWFormatter sotwFormatter;
    private ImageView arrowView;
    private ImageView roseView;
    private TextView sotwLabelDegrees;
    private TextView sotwLabelDirection;// SOTW is for "side of the world"

    private float currentAzimuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        sotwFormatter = new SOTWFormatter(this);

        arrowView = findViewById(R.id.main_image_hands);
        sotwLabelDegrees = findViewById(R.id.sotw_label_degree);
        sotwLabelDirection = findViewById(R.id.sotw_label_direction);
        roseView = findViewById(R.id.main_image_dial);

        setupCompass();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start compass");
        compass.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        compass.stop();
    }

    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = getCompassListener();
        compass.setListener(cl);
    }

    private void adjustRose(float azimuth) {

        Animation rotateRose = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);


        rotateRose.setDuration(500);
        rotateRose.setRepeatCount(0);
        rotateRose.setFillAfter(true);


        roseView.startAnimation(rotateRose);
        currentAzimuth = azimuth;


    }

    private void adjustSotwLabel(float azimuth) {
        sotwLabelDegrees.setText(sotwFormatter.formatDegree(azimuth));
        sotwLabelDirection.setText(sotwFormatter.formatDirection(azimuth));
    }

    private Compass.CompassListener getCompassListener() {
        return new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(final float azimuth) {
                // UI updates only in UI thread
                // https://stackoverflow.com/q/11140285/444966
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adjustRose(azimuth);
                        adjustSotwLabel(azimuth);
                    }
                });
            }
        };
    }
}

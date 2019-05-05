package com.sevencrayons.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Compass implements SensorEventListener {

    public interface CompassListener {
        void onNewAzimuth(float azimuth);
    }

    private CompassListener listener;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor magneticSensor;

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] R = new float[9];
    private float[] I = new float[9];

    private float azimuth;


    public Compass(Context context) {
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start() {
        sensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magneticSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }


    public void setListener(CompassListener l) {
        listener = l;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.99f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                for (int i = 0; i < 3; i++) {
                    mGravity[i] = alpha * mGravity[i] + (1 - alpha)
                            * event.values[i];
                }

            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {


                for (int i = 0; i < 3; i++) {
                    mGeomagnetic[i] = alpha * mGeomagnetic[i] + (1 - alpha)
                            * event.values[i];
                }

            }

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth +360) % 360;
                if (listener != null) {
                    listener.onNewAzimuth(azimuth);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

package com.group13.dynamicwayfinder.Utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import java.util.ArrayList;


import com.group13.dynamicwayfinder.Activities.Map.MapActivity;

import androidx.annotation.Nullable;


public class AccelerometerClass extends Service implements SensorEventListener {



        private SensorManager sensorManager;
        private Sensor accel;
        private MapActivity mapActivity;
        private int count;
        private int trigger;
        float x;
        float y;
        float z;
        double meanValue;
        Context mapContext;


    public AccelerometerClass(MapActivity mapActivity, Context mapContext){
            this.mapActivity=mapActivity;
            this.mapContext=mapContext;

        }


        public void setup(){


            sensorManager = (SensorManager) mapContext.getSystemService(SENSOR_SERVICE);

            count = 0;


            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);


        }

        @Override
        public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    trigger++;
                    //average acceleration in XYZ direction
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];
                    meanValue = (x+y+z)/3.0;
                    if (trigger >= 200) {
                        trigger=0;
                        if (meanValue < 5) {
                            count++;
                            if (count > 1000) {
                                resetRoute();
                                System.out.println("Route was stagnant: " + meanValue);
                            }
                        }else{
                            count=0;
                            System.out.println("Acceleration recorded, reset Count: " + meanValue);
                        }
            }


        }

    private void resetRoute() {
        //reset route
        mapActivity.callServer();
    }


    @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

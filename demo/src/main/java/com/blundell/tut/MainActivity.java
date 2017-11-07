package com.blundell.tut;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.blundell.zxsensor.ZxSensor;

public class MainActivity extends Activity implements ZxSensor.GestureListener {

    private static final String GESTURE_SENSOR = "UART0";

    private ZxSensor zxSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        zxSensor = ZxSensor.newInstance(GESTURE_SENSOR);
        zxSensor.setListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        zxSensor.startListeningForGestures();
    }

    @Override
    public void onSwipeLeft() {
        Log.d("TUT", "Swipe Left");
    }

    @Override
    public void onSwipeRight() {
        Log.d("TUT", "Swipe Right");
    }

    @Override
    protected void onStop() {
        zxSensor.stopListeningForGestures();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        zxSensor.close();
        super.onDestroy();
    }

}

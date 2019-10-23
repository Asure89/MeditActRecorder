package com.example.liya.medicinecalendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class SaveSensorReceiver extends BroadcastReceiver {

    private static final String TAG="SaveSensorReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        //Create an intent to the alarmService
       Intent serviceIntent = new Intent(context,SaveSensorService.class);

        Log.d(TAG,"onReceive: SaveSensor.");

        //stop the intent
        context.stopService(serviceIntent);


    }
}

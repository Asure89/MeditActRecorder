package com.example.liya.medicinecalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "SaveSenorReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

        //Create an intent to the alarmService
        Intent alarmIntent = new Intent(context,AlarmService.class);

        Log.d(TAG,"onReceive: alarm.");
        //start the intent
        context.startService(alarmIntent);
    }
}


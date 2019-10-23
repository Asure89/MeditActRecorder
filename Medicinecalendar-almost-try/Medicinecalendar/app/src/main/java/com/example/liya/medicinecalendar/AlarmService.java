package com.example.liya.medicinecalendar;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.security.Provider;

public class AlarmService extends Service{
    private MediaPlayer player;
    private Intent returnIntent;
    private static final String TAG = "AlarmService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(AlarmService.this, "AlarmService onBind.", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        //set the time length of ring without being stopped. now: keep ringing if no confirm
        //player.setLooping(true);
        player.start();
        Log.d(TAG, "onStartCommand: Alarm started.");
        Toast.makeText(AlarmService.this,"Wecker startet.",Toast.LENGTH_SHORT).show();

        //stop alarm after not being stop for 3 min
        new android.os.Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                stopSelf();
            }
        },1000*60*3);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    //Intent returnIntent = new Intent(AlarmService.this, MainActivity.class);
    player.stop();
    //startActivity(returnIntent);
    Log.d(TAG, "onStartCommand: Alarm stopped.");
    Toast.makeText(AlarmService.this,"Wecker endet.",Toast.LENGTH_SHORT).show();
    }
}
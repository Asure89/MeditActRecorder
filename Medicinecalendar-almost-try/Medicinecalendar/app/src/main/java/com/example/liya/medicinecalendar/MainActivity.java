package com.example.liya.medicinecalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.getInstance;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // show screen view
    private TextView LastTime;
    private Button btnRecord;
    private TextView NextTime;

    // get time
    private Calendar calendar, sensorCalendar;
    private SimpleDateFormat Day_Format, Time_Format;
    private String Day,Time,NewTime;

    // save intermediate data
    public static final String SHARED_PREFS = "sharedPrefs";

    //to update app view when restart the app
    private String ShowLastTime;
    private String ShowNextTime;

    // set the alarm
    AlarmManager alarmManager;
    Context alarm_context, service_context;
    PendingIntent pending_alarmIntent, pending_serviceIntent;
    Intent alarm_Intent,service_Intent;

    @Override
    //first view of the app
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LastTime = (TextView) findViewById(R.id.lasttime);
        btnRecord = (Button) findViewById(R.id.btnRecord);
        NextTime = (TextView) findViewById(R.id.nexttime);
        this.alarm_context = this;
        this.service_context = this;

        /******************To show the textview after restart START**************************/
        //share prefs data
        SharedPreferences sharedPreferences = getSharedPreferences("myTime", 0);

        //keep view
        loadData();
        updateViews();
        /******************To show the textview after restart END**************************/


        //initialize the alarm manager
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //create a content to the alarm receiver
        final Intent alarm_Intent = new Intent(this.alarm_context, AlarmReceiver.class);
        final Intent service_Intent = new Intent(this.service_context, SaveSensorReceiver.class);

        //define the action of the bottom
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, AlarmService.class));

                //get current time
                calendar = Calendar.getInstance();
                Day_Format = new SimpleDateFormat("dd.MM.yyyy");
                Time_Format = new SimpleDateFormat("HH:mm");

                Day = Day_Format.format(calendar.getTime());
                Time = Time_Format.format(calendar.getTime());

                //show current time and set next time
                LastTime.setText("Letzte Einnahme ist um " + Time + " am " + Day);

                /****************************set text and ring to remind for the next time to take medicine START*****************************/
                calendar.add(Calendar.HOUR_OF_DAY, 4);//for example: the medicine should be taken every 4 hours
                //calendar.add(Calendar.SECOND, 10);//for test
                Log.d(TAG, "onClick:" + calendar);
                //now the NextTime is saved in 'calendar'
                NewTime = Time_Format.format(calendar.getTime());

                //implement of next time
                if (NewTime.compareTo("21:00") > 0 || NewTime.compareTo("08:00") < 0) {
                    NewTime = "08:00";//don't set the alarm at sleeping time
                    calendar.set(Calendar.HOUR_OF_DAY, 8);//next time ring at 8am
                    calendar.set(Calendar.MINUTE, 00);
                }
                NewTime = Time_Format.format(calendar.getTime());
                 NextTime.setText("Nächste Einnahme um " + NewTime);
                Toast.makeText(MainActivity.this,"Diese Einnahme ist bestätigt. Nächstes Mal um "
                        + NewTime+".",Toast.LENGTH_LONG).show();

                //set a delayed time to alarmService
               PendingIntent pending_alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarm_Intent, PendingIntent.FLAG_UPDATE_CURRENT);
               alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_alarmIntent);
                Log.d(TAG,"onClick: alarm start time set.");
                /************************set text and ring to remind for the next time to take medicine END************************/



                /***********************************implement saveSensorService START*********************************************/
                //set the time to stop saveSensorService
                sensorCalendar = Calendar.getInstance();
                sensorCalendar.add(Calendar.MINUTE,30);
                //sensorCalendar.add(Calendar.SECOND,5);//for test

                //set a delayed time to SaveSensorService
                PendingIntent pending_serviceIntent = PendingIntent.getBroadcast(MainActivity.this, 0, service_Intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, sensorCalendar.getTimeInMillis(), pending_serviceIntent);
                Log.d(TAG,"onClick: service stop time set.");
                /***********************************implement saveSensorService END*********************************************/


                Intent SaveSensor_intent = new Intent(MainActivity.this, SaveSensorService.class);
                startService(SaveSensor_intent);
                Log.d(TAG, "onClick: go to service");
            }
        });
    }

    @Override
    protected void onStop () {
        super.onStop();

        //save it to sharePreference
        saveData();

    }

    /*****************************************keep the screen view when the app restart - START**************************************************************/
    public void saveData(){

        //save current input data to sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MYLastTime", LastTime.getText().toString());
        editor.putString("MYNextTime", NextTime.getText().toString());

        editor.apply();
        }

    //show the previous data when restarting the app
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        ShowLastTime = sharedPreferences.getString("MYLastTime","");
        ShowNextTime = sharedPreferences.getString("MYNextTime","");
    }
    //update the initial view
    public void updateViews() {
        LastTime.setText(ShowLastTime);
        NextTime.setText(ShowNextTime);
    }
    /*****************************************keep the screen view when the app restart -END **************************************************************/


}

package com.example.liya.medicinecalendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import android.os.Handler;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class SaveSensorService extends Service implements SensorEventListener {

    private static final String TAG = "SaveSensorService";

    Float xValue, yValue, zValue, xGyroValue, yGyroValue, zGyroValue;
    SensorManager sensorManager;
    private Sensor accelerometer, mGyro;


    private SimpleDateFormat Date_Format,Newtime_Format;
    private  Calendar calendar;
    private String CurrentTime,FileName;

    /*
    final class MyThreadClass implements Runnable {
        int service_id;

        MyThreadClass(int service_id) {
            //initialize the variable
            this.service_id = service_id;
        }

        @Override
        public void run() {
            int i = 0;
            {

                try
                //to prevent that the file fail to be read or created, use catch to print the error
                {
                    //append the sensor data in the file for 30min
                    while (i < 30*60) {
                        Newtime_Format = new SimpleDateFormat("HHmmss");
                        CurrentTime = Newtime_Format.format(calendar.getTime());
                        fos.write((CurrentTime + "\t" + xValue + "\t" + yValue + "\t"
                                + zValue + "\t" + xGyroValue + "\t" + yGyroValue + "\t" + zGyroValue + "\r\n").getBytes());

                        wait(100);
                      i++;
                    }

                    Log.d(TAG, "onClick: file ist written.");
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: SaveSensorService started.");


        //set the sensor to capture the movement of the patient - accelerometer and  gyroscope
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(SaveSensorService.this, accelerometer,1000*1000);
            Log.d(TAG, "onCreate: Registered accelerometer Listener");
        } else {
            xValue = null;
            yValue = null;
            zValue = null;

        }

        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mGyro != null) {
            sensorManager.registerListener(SaveSensorService.this, mGyro, 1000*1000);
            Log.d(TAG, "onCreate: Registered Gyro Listener");

        } else {
            xGyroValue = null;
            yGyroValue = null;
            zGyroValue = null;
        }
        Log.d(TAG, "onCreate:Sensor Services initialized.");

        if (mGyro == null && accelerometer == null){
            stopSelf();
            Log.d(TAG, "onStartCommand: Device not support");
            Toast.makeText(SaveSensorService.this, "Device not support",Toast.LENGTH_SHORT).show();
        }

        Date_Format = new SimpleDateFormat("yyyyMMddHHmm");
        calendar = Calendar.getInstance();
        FileName = Date_Format.format(calendar.getTime()) + ".txt";
        Toast.makeText(SaveSensorService.this, FileName + " SaveSensorService is running...", Toast.LENGTH_SHORT).show();

        //create a new file, named by the current time
        // in one minute only one file will be created
        try
        //to prevent that the file fail to be read or created, use catch to print the error
        {
            FileOutputStream fos = null;
            fos = openFileOutput(FileName, MODE_APPEND);

            //write the first line - description
            fos.write("xValue\tyValue\tzValue\txGyroValue\tyGyroValue\tzGyroValue\r\n".getBytes()); //define the first line
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

            //run the parallel thread and start to save the data
        /*    Thread thread = new Thread(new MyThreadClass(startId));
            thread.start(); */
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FileName, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Saved to " + getFilesDir() + "/" + FileName);

            return START_STICKY;
        }

        @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        CurrentTime = Newtime_Format.format((Calendar.getInstance()).getTime());
        Log.d(TAG, "onDestroy: SaveSensorService stopped.");
        Toast.makeText(SaveSensorService.this,"Sensordaten wurden gespeichert."+ CurrentTime,Toast.LENGTH_SHORT).show();
    }

    /******************************* set the data from sensors to variables - START ***************************/
    public void save() throws IOException {
        FileOutputStream fos = null;
        fos = openFileOutput(FileName, MODE_APPEND);
        fos.write((xValue+"\t"+yValue+"\t"+zValue+"\t"+xGyroValue+"\t"+yGyroValue+"\t"+zGyroValue+"\n").getBytes());
        fos.flush();
        fos.close();
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        //get sensor data

        if(sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            Log.d(TAG, "onSensorChanged: \tX:" + sensorEvent.values[0] + "\tY:" + sensorEvent.values[1] + "\tZ:" + sensorEvent.values[2]);

            xValue = sensorEvent.values[0];
            yValue = sensorEvent.values[1];
            zValue = sensorEvent.values[2];


        }

        //put gyrocope data on layout
        else if(sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            Log.d(TAG, "onSensorChanged: \tXG:" + sensorEvent.values[0] + "\tYG:" + sensorEvent.values[1] + "\tZG:" + sensorEvent.values[2]);

            xGyroValue = sensorEvent.values[0];
            yGyroValue = sensorEvent.values[1];
            zGyroValue = sensorEvent.values[2];

        }
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /******************************* set the data from sensors to variables - END ***************************/

}

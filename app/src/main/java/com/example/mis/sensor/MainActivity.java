package com.example.mis.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.mis.sensor.FFT;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //example variables
    private double[] rndAccExamplevalues;
    private double[] freqCounts;
    private SensorManager sensorManager;
    Sensor accelerometer;
    private static final String TAG="Main Activity";
    TextView xValue,yValue,zValue;
    //References
    //https://developer.android.com/guide/topics/sensors/sensors_overview.html
    //https://www.youtube.com/watch?v=pkT7DU1Yo9Q&list=LLRkVz3oUd1GQ1KNwXBC_NqA&index=3
    //https://www.youtube.com/watch?v=Rda_5s4rObQ&list=LLRkVz3oUd1GQ1KNwXBC_NqA&index=2
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xValue= (TextView) findViewById(R.id.xValue);
        yValue= (TextView) findViewById(R.id.yValue);
        zValue= (TextView) findViewById(R.id.zValue);

        sensorManager =(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this,accelerometer,sensorManager.SENSOR_DELAY_NORMAL);


        //initiate and fill example array with random values
        rndAccExamplevalues = new double[64];
        randomFill(rndAccExamplevalues);
        new FFTAsynctask(64).execute(rndAccExamplevalues);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor,int i){

    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
      // Log.d (TAG,"X"+ sensorEvent.values[0]+"Y"+ sensorEvent.values[1]+"Z"+ sensorEvent.values[2]);
        xValue.setText("X-acc" +sensorEvent.values[0]);
        yValue.setText("Y-acc" +sensorEvent.values[1]);
        zValue.setText("Z-acc" +sensorEvent.values[2]);
    }


    /**
     * Implements the fft functionality as an async task
     * FFT(int n): constructor with fft length
     * fft(double[] x, double[] y)
     */

    private class FFTAsynctask extends AsyncTask<double[], Void, double[]> {

        private int wsize; //window size must be power of 2

        // constructor to set window size
        FFTAsynctask(int wsize) {
            this.wsize = wsize;
        }

        @Override
        protected double[] doInBackground(double[]... values) {


            double[] realPart = values[0].clone(); // actual acceleration values
            double[] imagPart = new double[wsize]; // init empty

            /**
             * Init the FFT class with given window size and run it with your input.
             * The fft() function overrides the realPart and imagPart arrays!
             */
            FFT fft = new FFT(wsize);
            fft.fft(realPart, imagPart);
            //init new double array for magnitude (e.g. frequency count)
            double[] magnitude = new double[wsize];


            //fill array with magnitude values of the distribution
            for (int i = 0; wsize > i ; i++) {
                magnitude[i] = Math.sqrt(Math.pow(realPart[i], 2) + Math.pow(imagPart[i], 2));
            }

            return magnitude;

        }

        @Override
        protected void onPostExecute(double[] values) {
            //hand over values to global variable after background task is finished
            freqCounts = values;
        }
    }




    /**
     * little helper function to fill example with random double values
     */
    public void randomFill(double[] array){
        Random rand = new Random();
        for(int i = 0; array.length > i; i++){
            array[i] = rand.nextDouble();
        }
    }



}

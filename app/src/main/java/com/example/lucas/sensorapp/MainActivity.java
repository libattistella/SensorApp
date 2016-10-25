package com.example.lucas.sensorapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mPressure;
    private Sensor mLight;
    private Sensor mTemperature;

    TextView mDataPressure;
    TextView mDataTemperature;
    TextView mDataLight;
    List<Sensor> deviceSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }

        mDataPressure = (TextView) findViewById(R.id.dataPressure);
        mDataTemperature = (TextView) findViewById(R.id.dataTemperature);
        mDataLight = (TextView) findViewById(R.id.dataLight);

        mDataTemperature.setText("No se puede obtener este valor");
        mDataPressure.setText("No se puede obtener este valor");
        mDataLight.setText("No se puede obtener este valor");

        int brightnessMode = 0;
        try {
            brightnessMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Do something with this sensor data.
        switch (event.sensor.getType()){
            case Sensor.TYPE_PRESSURE:
                float millibars_of_pressure = event.values[0];
                mDataPressure.setText(String.valueOf(millibars_of_pressure));
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                float celcius_degrees = event.values[0];
                mDataTemperature.setText(String.valueOf(celcius_degrees));
                break;
            case Sensor.TYPE_LIGHT:
                float lux_units = event.values[0];
                mDataLight.setText(String.valueOf(lux_units));
                refreshBrightness(lux_units);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        //This prevents a sensor from continually sensing data and draining the battery
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void refreshBrightness(float brightness) {

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (brightness < 0) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            brightness = brightness / 32;
            lp.screenBrightness = brightness;
        }
        getWindow().setAttributes(lp);
    }
}

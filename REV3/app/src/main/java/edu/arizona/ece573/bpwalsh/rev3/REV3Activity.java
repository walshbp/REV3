//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
package edu.arizona.ece573.bpwalsh.rev3;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.SocketException;

//
// The Activity class is the main class of the android application all UI events go through this
// class. The Activity class is responsible for forwarding the UI events to the CommandProcessor.
//
public class REV3Activity extends AppCompatActivity implements SensorEventListener,
        ConfigurationDialog.ConfigurationDialogListener, AboutDialog.AboutDialogListener {

    private CommandProcessor m_commandProcessor;
    private SensorManager m_sensorManager;
    private Sensor m_accelerometer;
    private Sensor m_gyro;
    private ProtocolProcessor m_protocolProcessor;
    private ConfigurationDialog m_configurationDialog;
    private boolean m_disableSensors = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set screen visibility
        //View decorView = this.getWindow().getDecorView();
        //int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_rev3);

        // If debug logging is enabled, make sure we have permission to write to external storage
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        boolean logging_enabled = pm.getBoolean("debugInfoEnabled", false);
        if (logging_enabled) {
            // Check Permissions
            // Android Marshmallow silliness, still need to ask for explicit permission, even through
            // it is given in the manifest
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                final int REQUEST_ID = 1;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ID);
            }
        }

        // see if we have a accelerometer.
        m_sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){
            Toast.makeText(this, "Accelerometer Sensor Not Available.", Toast.LENGTH_SHORT).show();
        }

        // do we have a gyro...
        if (m_sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null){
            Toast.makeText(this, "Gyro Sensor Not Available.", Toast.LENGTH_SHORT).show();
        }

        // bring up the configuration dialog
        m_configurationDialog = new ConfigurationDialog();
        m_configurationDialog.show(this.getFragmentManager(), "ConfigurationDialog");

    }

    @Override
    protected void onStart()
    {

        super.onStart();
    }

    @Override
    protected void onResume()
    {
        //View decorView = getWindow().getDecorView();
        //int uiOptions =  View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);
        if (m_accelerometer != null) {
            m_sensorManager.registerListener(this, m_accelerometer, SensorManager.SENSOR_DELAY_GAME);

        }
        if (m_gyro != null) {
            m_sensorManager.registerListener(this, m_gyro, SensorManager.SENSOR_DELAY_GAME);
        }
        if (m_protocolProcessor != null)
            m_protocolProcessor.resume();
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        // stop protocol processor and command processor so that logs can close properly
        if (m_protocolProcessor != null) {
            m_protocolProcessor.disconnect();
        }
        if (m_commandProcessor != null) {
            m_commandProcessor.stop();
        }
        super.onStop();
    }

    @Override
    protected void onPause()
    {
        m_sensorManager.unregisterListener(this);
        super.onPause();
    }


    //
    // Action Bar Menu Items
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add in main menu  to action bar with a "about" menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_about_rev3)
        {
            DialogFragment dialog = new AboutDialog();
            dialog.show(this.getFragmentManager(), "AboutDialog");

            return true;
        }
        else if (id == R.id.action_rev3_preferences)
        {
            Intent intent = new Intent(this, REV3Preferences.class);
            startActivity(intent);

            return true;
        }
        else if (id == R.id.action_simulate_disconnect)
        {
            this.simulateDisconnect();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    //
    // GuI Power and Turn Commands
    //
    public void onTurnLeftClickEvent(View view) {
        Log.v("REV3Activity","Turn Left Click Event");
        boolean ret = false;
        try {
            ret = m_commandProcessor.leftButtonClickEvent();

            if (ret) {
                this.setTurnRateTextView();
            }
        } catch (SocketException e) {
            this.handleDisconnect();
        }
    }

    public void onTurnRightClickEvent(View view) {
        Log.v("REV3Activity","Turn Right Click Event");
        try {
            boolean ret = m_commandProcessor.rightButtonClickEvent();
            if (ret) {
                this.setTurnRateTextView();
            }
        } catch (SocketException e) {
            this.handleDisconnect();
        }
    }

    public void onForwardPowerClickEvent(View view) {
        Log.v("REV3Activity","Forward Click Event");
        try {


            boolean ret = m_commandProcessor.forwardButtonClickEvent();
            if (ret) {
                this.setSpeedTextView();
            }
        } catch (SocketException e) {
            this.handleDisconnect();
        }
    }

    public void onBackwardPowerClickEvent(View view) {
        Log.v("REV3Activity", "Turn Backward Event");
        try {
            boolean ret = m_commandProcessor.backwardButtonClickEvent();
            if (ret) {
                this.setSpeedTextView();
            }

        } catch (SocketException e) {
            this.handleDisconnect();
        }
    }

    //
    // Sensor support functions
    //
    @Override
    public void onSensorChanged(SensorEvent e) {
        //Log.v("REV3Activity", "Sensor Event: " + e);

        boolean ret = this.processSensorEvent(e.timestamp, e.sensor.getType(), e.values[0],
                e.values[1], e.values[2]);
        if (ret) {
            this.setTurnRateTextView();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // nothing to do.
    }

    // processSensorEvent pulled out of onSensorChanged to support unit tests
    public boolean processSensorEvent( long timestamp, int sensor_type, float v0, float v1, float v2) {

        boolean ret = false;
        try{
            ret = m_commandProcessor.processSensorEvent(timestamp, sensor_type, v0, v1, v2);

        } catch (SocketException e) {
            this.handleDisconnect();
        }
        return ret;
    }

    //
    // Mutators to update GUI elements with latest application state
    //

    public void setSpeedTextView()
    {
        TextView ptext = (TextView) this.findViewById(R.id.powerCommandedText);
        ptext.setText(String.format("Speed: %+4.0f%%", this.getTransmittedPowerCommand()));
    }

    public void setTurnRateTextView()
    {
        TextView ptext = (TextView) this.findViewById(R.id.turnCommandedText);
        ptext.setText(String.format("Turn Rate: %+4.0f%%", this.getTransmittedTurnCommand()));
    }

    public void displayDeltaHeading(double deltaHeading)
    {
        //Log.v("MYTAG","Delta Heading " + deltaHeading);
        ImageView image = (ImageView) this.findViewById(R.id.turnIndicator);
        image.setRotation((float)deltaHeading);
    }

    //
    // Configuration Dialog Listener
    //

    @Override
    public void onConfigurationDialogOkay(ConfigurationDialog dialog) {
        Log.v("REV3Activity","Config Okayt");
        Log.v("DIALOG", "OKAY ACCPTED: " );
        // save off host and port into application preferences
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = pm.edit();
        editor.putString("ip_address", dialog.getEV3Host());
        editor.putString("port", dialog.getEV3Port().toString()); //FIXME: Should be putInt
        editor.commit();

        // Set up protocol processor
        m_protocolProcessor = new ProtocolProcessor(this);
        m_commandProcessor = new CommandProcessor(this, m_protocolProcessor);

        if (!m_disableSensors) {
            if (m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                m_accelerometer = m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                m_sensorManager.registerListener(this, m_accelerometer, SensorManager.SENSOR_DELAY_GAME);

            }
            if (m_sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
                m_gyro = m_sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                m_sensorManager.registerListener(this, m_gyro, SensorManager.SENSOR_DELAY_GAME);
            }
        }


        m_protocolProcessor.connect(dialog.getEV3Host(), dialog.getEV3Port());

        this.setSpeedTextView();
        this.setTurnRateTextView();
    }

    //
    // About Dialog Listener
    //
    @Override
    public void onAboutDialogOkay(AboutDialog dialog) {
        // Do nothing
    }

    //
    // Accessors to support unit tests
    //
    public float getTransmittedPowerCommand()
    {
        float ret = -999.0f;
        if (m_protocolProcessor != null) {
            ret = m_protocolProcessor.getTransmittedPowerCommand();
        }
        return ret;
    }


    public float getTransmittedTurnCommand()
    {
        float ret = -999.0f;
        if (m_protocolProcessor != null) {
            ret = m_protocolProcessor.getTransmittedTurnCommand();
        }
        return ret;
    }

    public ProtocolProcessor.ReceivedMsg getReceivedDeltaHeading() {
        ProtocolProcessor.ReceivedMsg ret = null;

        if (m_protocolProcessor != null) {
            ret = m_protocolProcessor.getReceivedDeltaHeading();
        }
        return ret;
    }
    public boolean isConnected()
    {
        boolean ret = false;
        if (m_protocolProcessor != null) {
            ret = m_protocolProcessor.isConnected();
        }
        return ret;
    }

    public void disableSensors()
    {
        m_disableSensors = true;
        m_sensorManager.unregisterListener(this);
    }

    public void setTurnSource(TurnProcessor.DataSource src)
    {
        if (m_commandProcessor != null) {
            m_commandProcessor.setTurnSource(src);
        } else {
            Toast.makeText(this, "Error: attempting to set turn source before connected!", Toast.LENGTH_SHORT).show();
        }
    }

    public float getTurnProcessorCommand() {
        float ret = 0.0f;
        if (m_commandProcessor != null) {
            ret = m_commandProcessor.getTurnProcessorCommand();
        }
        return ret;
    }

    public ConfigurationDialog getConfigurationDialog()
    {
        return m_configurationDialog;
    }


    //
    // Disconnect Use Case
    //
    public void simulateDisconnect()
    {
        m_protocolProcessor.simulateDisconnect();
    }


    public void handleDisconnect() {


        Toast.makeText(this, "Error: REV3 disconnected from the EV3!", Toast.LENGTH_SHORT).show();

        // stop protocol processor and command processor so that logs can close properly
        if (m_protocolProcessor != null) {
            m_protocolProcessor.disconnect();
        }
        if (m_commandProcessor != null) {
            m_commandProcessor.stop();
        }

        // destroy all processors.
        m_protocolProcessor = null;
        m_commandProcessor = null;

        m_sensorManager.unregisterListener(this);

        // bring up the configuration dialog to restart
        m_configurationDialog = new ConfigurationDialog();
        m_configurationDialog.show(this.getFragmentManager(), "ConfigurationDialog");
    }


}

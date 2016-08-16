//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
package edu.arizona.ece573.bpwalsh.rev3;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;


//
// Helper class for the CommandProcessor. The TurnProcessor is responsible for interpreting “Left”
// and “Right” button clicks, and the “TYPE_ACCELEROMETER” and “TYPE_GYRO” sensor inputs, in order
// to generate the appropriate Turn commands.
//

public class TurnProcessor {

    public enum DataSource {GUI, SENSOR}

    private double m_sensorCommand = 0.0;
    private double m_guiCommand = 0.0;
    DataSource m_source;
    TurnFilter m_filter;
    TurnProcessor(Context context)
    {
        m_filter = new TurnFilter(context);
        m_source = DataSource.SENSOR;
    }

    void stop()
    {
        m_filter.stop_recorder();
    }

    public void setTurnSource(TurnProcessor.DataSource src)
    {
        m_source = src;
    }

    double processSensorEvent(long timestamp, int sensor_type, float v0, float v1, float v2)
    {
        final double RAD2DEG = 180.0 / Math.PI;

        // if we are being controlled to GUI and the device has moved sufficiently, switch to
        // SENSOR control.
        if (sensor_type == Sensor.TYPE_GYROSCOPE && m_source == DataSource.GUI) {
            double rate = v2 * RAD2DEG;
            if (rate > 125.0) {
                Log.v("GUITurnProcessor", "Setting Turn Source to Sensor");
                m_source = DataSource.SENSOR;
            }
        }

        double est_roll = m_filter.filter(timestamp, sensor_type, v0, v1, v2);

        // map estimated roll to sensor command convention
        m_sensorCommand = Math.round( - est_roll * 100.0 / 90.0 * RAD2DEG / 5.0 ) * 5.0 ;
        if (m_sensorCommand > 100.0) {
            m_sensorCommand = 100.0;
        }
        else if (m_sensorCommand < -100.0) { // bounds check
            m_sensorCommand = -100.0;
        }

        return est_roll;
    }

    public boolean processLeftTurnEvent()
    {
        if (m_source == DataSource.SENSOR)
        {
            // round our last sensor command to the nearest 20, in order to be consistent with GUI
            // scheme
            m_guiCommand = Math.round(m_sensorCommand/20.0) * 20.0;
        }
        m_guiCommand += 20.0f;
        if (m_guiCommand > 100.0f) {  // bounds check
            m_guiCommand = 100.0f;
        }
        m_source = DataSource.GUI;
        //Log.v("GUITurnProcessor", "Turn Command" + m_guiCommand);
        return true;
    }

    public boolean processRightTurnEvent()
    {
        if (m_source == DataSource.SENSOR)
        {
            // round our last sensor command to the nearest 20, in order to be consistent with GUI
            // scheme
            m_guiCommand = Math.round(m_sensorCommand/20.0) * 20.0;
        }
        m_guiCommand -= 20.0f;
        if (m_guiCommand < -100.0f)
        {
            m_guiCommand = -100.0f;
        }
        m_source = DataSource.GUI;
        //Log.v("GUITurnProcessor", "Turn Command" + m_guiCommand);
        return true;
    }

    public float getTurnCommand()
    {
        float ret;

        if (m_source == DataSource.GUI) {
            ret = (float) m_guiCommand;
        }
        else {
            ret = (float)m_sensorCommand;
        }
        //Log.v("GUITurnProcessor", "GetTurnCommand: " + ret);
        return ret;
    }



    private class TurnFilter {
        final double NS2S = 1.0 / 1000000000.0;
        final double RAD2DEG = 180.0 / Math.PI;
        final double m_factor = 0.05; // blending factor

        double m_accRollEst = 0.0;
        double m_filterRollEst = 0.0;
        long m_lastTime = 0;
        boolean m_initialized = false;
        private DataRecorder m_recorder;

        public TurnFilter(Context context) {

            m_recorder = new DataRecorder(context, "sensor_filter_log.txt");
        }

        public void stop_recorder()
        {
            m_recorder.close();
        }

        public double filter(long timestamp, int sensor_type, float v0, float v1, float v2)
        {

            if (sensor_type == Sensor.TYPE_ACCELEROMETER)
            {
                m_accRollEst =  Math.atan2((double)v1, (double) v0);
                //Log.d("SensorTurnTest", "roll est: " + m_accRollEst);
                if (!m_initialized) {
                    m_filterRollEst = m_accRollEst;
                    m_initialized = true;
                }
            }
            else if (sensor_type == Sensor.TYPE_GYROSCOPE)
            {
               //Log.d("SensorTurnTest", "lasttime: " + m_lastTime);
                if (m_lastTime != 0 && m_initialized)
                {
                    //Log.d("SensorTurnTest", "in if: " + m_lastTime);
                    double dt  = (timestamp - m_lastTime) * NS2S;
                    double pre_est = m_filterRollEst - v2 * dt;
                    //Log.d("SensorTurnTest", "dt: " + dt);
                    if (Math.abs(pre_est- m_accRollEst) < Math.PI) {
                        // we have valid estimate from accelerometer
                        // use complimentary filter, blending gyro and accelerometer measurements
                        m_filterRollEst = (1.0 - m_factor) *
                                pre_est + m_factor * m_accRollEst;
                        //Log.d("SensorTurnTest", "filter est: " + m_filterRollEst);
                    } else {
                        // our accelerometer estimate is invalid, may be beyond +/- 180 deg
                        // in these cases we will just use gyro inputs
                        m_filterRollEst = pre_est;
                    }
                }
                m_lastTime = timestamp;
            }
            m_recorder.printf("%d,%f,%f\n",
                    timestamp, m_filterRollEst * RAD2DEG, m_accRollEst * RAD2DEG);
            //Log.d("SensorTurnTest", "roll: " + m_filterRollEst);
            return m_filterRollEst;
        }

    }

}

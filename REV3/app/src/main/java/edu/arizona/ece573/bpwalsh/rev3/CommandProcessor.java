//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
package edu.arizona.ece573.bpwalsh.rev3;

import android.content.Context;
import android.hardware.Sensor;
import android.widget.TextView;

import java.net.SocketException;

import static java.lang.Math.abs;

//
// The CommandProcessor class processes button clicks from the main control screen, as well as
// sensor inputs. These inputs are converted to appropriate Turn or Set Power Level commands
// and are forwarded to the Protocol Processor.
//
public class CommandProcessor {
    private PowerProcessor m_powerProcessor = new PowerProcessor();
    private TurnProcessor m_turnProcessor;
    private ProtocolProcessor m_protocolProcessor;

    private float m_oldTurnCommand = 0.0f;
    private float m_oldPowerCommand = 0.0f;
    private float m_turnProcessorCommand = 0.0f;
    final private float m_powerThreshold = 5.0f;
    final private float m_turnThreshold = 5.0f;
    private DataRecorder m_recorder;

    public CommandProcessor(Context context, ProtocolProcessor p)
    {

        m_protocolProcessor = p;
        m_recorder = new DataRecorder(context, "sensor_data_log.txt");
        m_turnProcessor = new TurnProcessor(context);
    }

    public void stop()
    {
        m_recorder.close();
        m_turnProcessor.stop();
    }
    //
    // GUI Event handling
    //
    public boolean leftButtonClickEvent() throws SocketException {
        m_turnProcessor.processLeftTurnEvent();
        return this.queueTurnCommand(m_turnProcessor.getTurnCommand());
    }

    public boolean rightButtonClickEvent() throws SocketException {
        m_turnProcessor.processRightTurnEvent();
        return this.queueTurnCommand(m_turnProcessor.getTurnCommand());
    }

    public boolean forwardButtonClickEvent() throws SocketException {
        m_powerProcessor.processPowerUpEvent();
        return this.queuePowerCommand(m_powerProcessor.getPowerCommand());
    }

    public boolean backwardButtonClickEvent() throws SocketException {
        m_powerProcessor.processPowerDownEvent();
        return this.queuePowerCommand(m_powerProcessor.getPowerCommand());
    }

    //
    // Sensor Event Handling
    //
    public boolean processSensorEvent(long timestamp, int sensor_type, float v0, float v1, float v2) throws SocketException
    {
        this.logSensorEvent(timestamp, sensor_type, v0, v1, v2);
        m_turnProcessorCommand = (float) m_turnProcessor.processSensorEvent(timestamp, sensor_type, v0, v1, v2);
        return this.queueTurnCommand(m_turnProcessor.getTurnCommand());
    }

    //
    // helper functions
    //
    private boolean queueTurnCommand(float command) throws SocketException
    {
        boolean ret = false;
        // if threshold is met send to protocol processor
        if (abs(command - m_oldTurnCommand) >= m_turnThreshold)
        {
            m_protocolProcessor.queueTurnCommand(command);
            m_oldTurnCommand = command;
            ret = true;
        }
        return ret;
    }

    private boolean queuePowerCommand(float command) throws SocketException
    {
        boolean ret = false;
        // if threshold is met send to protocol processor
        if (abs(command - m_oldPowerCommand) >= m_powerThreshold)
        {
            m_protocolProcessor.queuePowerCommand(command);
            m_oldPowerCommand = command;
            ret = true;
        }
        return ret;
    }

    public void setTurnSource(TurnProcessor.DataSource src)
    {
        m_turnProcessor.setTurnSource(src);
    }


    void logSensorEvent(long timestamp, int sensor_type, float v0, float v1, float v2)
    {
        if (sensor_type == Sensor.TYPE_ACCELEROMETER)
        {
            m_recorder.printf("%d,%d,%f,%f,%f\n", timestamp, sensor_type, v0, v1, v2);
        }
        else if (sensor_type == Sensor.TYPE_GYROSCOPE)
        {
            m_recorder.printf("%d,%d,%f,%f,%f\n", timestamp, sensor_type, v0, v1, v2);
        }
    }

    public float getPowerCommand()
    {
        return m_oldPowerCommand;
    }

    public float getTurnCommand()
    {
        return m_oldTurnCommand;
    }

    public float getTurnProcessorCommand() {return m_turnProcessorCommand;}

}

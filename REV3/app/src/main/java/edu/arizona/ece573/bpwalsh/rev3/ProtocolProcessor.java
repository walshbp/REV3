//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
package edu.arizona.ece573.bpwalsh.rev3;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;

//
// The ProtocolProcessor is responsible for communicating and maintaining connection with the EV3
// Proxy. It receives commands from the command processor and transmits them to the EV3 Proxy. It
// also receives telemetry command (containing Delta Heading information) and forward the
// message on to be displayed by the Activity. (under construction)
//
public class ProtocolProcessor {

    private TransmitProxy  m_transmitProxy;
    private ReceiveProxy m_receiveProxy;
    private Context m_context;
    private String m_ipAddress;
    private int m_port;
    private boolean m_isConnected;
    private float m_transmittedPowerCommand = 0.0f;
    private float m_transmittedTurnCommand = 0.0f;
    private Double m_deltaHeading = 0.0;
    private boolean m_simulateDisconnect = false;

    public ProtocolProcessor(Context context) {
        m_context = context;
        m_transmitProxy = new TransmitProxy(context);
        m_port = 0;
        m_ipAddress = "0.0.0.0";
        m_isConnected = false;
    }


    public boolean isConnected()
    {
        return m_isConnected;
    }

    public void simulateDisconnect()
    {
        m_simulateDisconnect = true;
    }

    void connect(String ip_address, int port) {

        m_port = port;
        m_ipAddress = ip_address;

        // Startup Use Case - connect
        m_transmitProxy.connect(ip_address, port);

        // Startup Use Case - Send Init Message
        Integer receive_port = 0;
        m_transmitProxy.sendInitCommand(receive_port);

        // Startup Use Case - Start Receive Telemetry Task
        m_receiveProxy = new ReceiveProxy(m_context);

        m_isConnected = true;
    }

    void disconnect()
    {
        m_isConnected = false;
        m_transmitProxy.disconnect();
        m_receiveProxy.disconnect();
    }

    void resume() {
        m_receiveProxy = new ReceiveProxy(m_context);
    }

    // queue turn command to be sent
    void queueTurnCommand(float command) throws SocketException
    {
        m_transmitProxy.sendTurnCommand(command);
    }

    // queus power command to be sent
    void queuePowerCommand(float command) throws SocketException {
        m_transmitProxy.sendPowerCommand(command);
    }

    public float getTransmittedPowerCommand() {
        return m_transmittedPowerCommand;
    }

    public float getTransmittedTurnCommand(){
        return m_transmittedTurnCommand;
    }

    public ReceivedMsg getReceivedDeltaHeading() {
        return m_receiveProxy.m_receiveHandler.getDeltaHeadingMessage();
    }


    //
    //  The TransmitProxy will record all messages that would of been sent to an EV3.
    //
    private class TransmitProxy {
        private DataRecorder m_recorder;
        private Context m_context;
        private File m_outFile;
        public TransmitProxy(Context context)
        {
            m_context = context;
            m_recorder = null;
        }

        public void connect(String hostname, Integer port)
        {
            m_recorder  = new DataRecorder(m_context, "transmit_log.txt");
        }

        public void disconnect()
        {
            if (m_recorder != null) {
                m_recorder.close();
            }
        }

        public void sendInitCommand(Integer port)
        {
            if (m_recorder != null) {
                m_recorder.printf("INIT\n");
                m_recorder.printf("%d\n", port);
                //Log.v("FileFormatter", "Sending Init Command");
            }
        }

        public void sendTurnCommand(float command) throws SocketException {
            if (m_recorder != null) {
                m_recorder.printf("TURN\n");
                m_recorder.printf("%f\n", command);
                m_transmittedTurnCommand = command;
               // Log.v("FileFormatter", "Sending Turn Command");
            }

            if (m_simulateDisconnect)
            {
                throw new SocketException();
            }
        }

        public void sendPowerCommand(float command) throws SocketException {
            if (m_recorder != null) {
                m_recorder.printf("POWER\n");
                m_transmittedPowerCommand = command;
                m_recorder.printf("%f\n", command);
            }

            if (m_simulateDisconnect)
            {
                throw new SocketException();
            }
        }


    }

    public class ReceivedMsg {
        private boolean m_newMessage;
        private double m_value;

        ReceivedMsg(boolean isNew, double value) {
            m_newMessage = isNew;
            m_value = value;
        }

        boolean isNew() {
            return m_newMessage;
        }

        double getValue() {
            return m_value;
        }
    }

    //
    // The Receive Proxy will read simulated delta heading data from a file and propagate it through
    // the system as if it was a received "Delta Heading" message from an EV3.
    //
    private class ReceiveProxy {
        private Timer m_timer;
        private ReceiveTask m_receiveTask;
        private ReceiveHandler m_receiveHandler;

        // was 100 ms, raised to 250 ms to prevent starving emulator of processor time
        // during unit test execution.
        final private long m_delay = 250; // milliseconds

        public ReceiveProxy(Context c) {
            m_timer = new Timer();
            m_receiveHandler = new ReceiveHandler();
            m_receiveTask = new ReceiveTask(c);
            m_timer.schedule(m_receiveTask, m_delay, m_delay);

        }

        public void disconnect()
        {
            m_timer.cancel();
        }


        private class ReceiveTask extends TimerTask {
            private int start = 0;
            InputStream m_tmStream;
            BufferedReader m_tmReader;
            public ReceiveTask(Context c)
            {
                String file_name = "ev3_tm";
                m_tmStream = c.getResources().openRawResource(
                        c.getResources().getIdentifier( file_name,
                                "raw", c.getPackageName()));

                m_tmReader = new BufferedReader(new InputStreamReader(m_tmStream));
            }
            public void run() {
                //Log.d("MYTAG","Run task");
                Message msg = Message.obtain();
                String line;
                try {
                    if ((line = m_tmReader.readLine()) != null) {
                        String tm_header = line;
                    }
                    if ((line = m_tmReader.readLine()) != null) {
                        double delta_command = Float.parseFloat(line);
                        msg.what = (int) delta_command;
                        m_receiveHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private class ReceiveHandler extends Handler {
            private boolean m_newMessage = false;
            ReceivedMsg getDeltaHeadingMessage() {
                boolean isNew = m_newMessage;
                m_newMessage = false;
                return new ReceivedMsg(isNew, m_deltaHeading);
            }

            @Override
            public void handleMessage(Message msg) {
                //Log.d("MYTAG", String.format("Handler.handleMessage(): msg=%s", msg));
                m_deltaHeading = Double.valueOf(msg.what);

                REV3Activity act = (REV3Activity)m_context;
                act.displayDeltaHeading(m_deltaHeading);
                m_newMessage = true;
                super.handleMessage(msg);
            }
        }
    }


}

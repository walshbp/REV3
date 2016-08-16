//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
package edu.arizona.ece573.bpwalsh.rev3;

import android.util.Log;

//
// Helper class for the CommandProcessor. The PowerProcessor is responsible for interpreting
// “Forward” and “Backward” button clicks and generating the appropriate Set Power Level commands.
//
public class PowerProcessor {
    private float m_powerCommand = 0;

    public PowerProcessor()
    {
        // nothing to do
    }

    public boolean processPowerUpEvent()
    {
        m_powerCommand += 20.0f;
        if (m_powerCommand > 100.0f)
        {
            m_powerCommand = 100.0f;
        }
        Log.v("PowerProcessor", "Power Command" + m_powerCommand);
        return true;
    }

    public boolean processPowerDownEvent()
    {
        m_powerCommand -= 20.0f;
        if (m_powerCommand < -100.0f)
        {
            m_powerCommand = -100.0f;
        }
        Log.v("PowerProcessor", "Power Command" + m_powerCommand);
        return true;
    }

    public float getPowerCommand()
    {
        return m_powerCommand;
    }


}

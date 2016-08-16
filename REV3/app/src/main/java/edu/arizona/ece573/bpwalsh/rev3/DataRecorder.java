//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
package edu.arizona.ece573.bpwalsh.rev3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

//
// Helper class to help the various processors log their state. The logged data will then be used to
// support unit testing and requirement verification.
//
public class DataRecorder {
    private PrintWriter m_writer = null;
    private Context m_context;
    private File m_outFile;
    private static ExternalDir s_logDir = null;


    public DataRecorder(Context context, String file_name) {
        m_context = context;

        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(context);

        boolean logging_enabled = pm.getBoolean("debugInfoEnabled", false);
        Log.v("FileFormatter", "LoggingEnabled?  " + logging_enabled);
        if (logging_enabled) {

            // Delay initialization of static logdir so permissions can be set properly.
            if (s_logDir == null)
            {
                s_logDir = new ExternalDir();
            }
            // Get a directory to write out logging information to
            m_outFile = new File(s_logDir.getDirectory(), file_name);

            try {
                m_writer = new PrintWriter(m_outFile);
                Log.v("FileFormatter", "Opened  " + m_outFile.toString());
            } catch (IOException e) {
                m_writer = null;
                Log.v("FileFormatter", "Error opening:  " + m_outFile.toString());
            }
        }

    }

    void close() {
        if (m_writer != null) {
            m_writer.close();

            // make file written visible on external media.
            Intent intent =
                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(m_outFile));
            m_context.sendBroadcast(intent);
            m_writer = null;
        }
    }

    void printf(String format, Object... args) {
        if (m_writer != null) {
            m_writer.printf(format, args);
            m_writer.flush();
        }
    }
}
    class ExternalDir
    {
        private File m_dir;
        ExternalDir()
        {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd_hhmm_ss_a");
                m_dir = new File(Environment.getExternalStorageDirectory(),
                        "REV3/LOGS_" + date_format.format(new Date()));
                m_dir.mkdirs();
                // create it, if it doesn't exist
                if (!m_dir.exists()) {
                    Log.v("FileFormatter", "Error creating external directory: " + m_dir.toString());
                }
            }
        }

        public File getDirectory()
        {
            return m_dir;
        }

    }


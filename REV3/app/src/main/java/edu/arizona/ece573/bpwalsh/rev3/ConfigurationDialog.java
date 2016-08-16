//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
package edu.arizona.ece573.bpwalsh.rev3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//
// The ConfigurationDialog class is used to prompt the user for the IP address and port of the
// EV3 robot to connect to.
//
public class ConfigurationDialog extends DialogFragment {


    private EditText m_hostText;
    private EditText m_portText;

    private String m_ipAddress;
    private int m_port;

    ConfigurationDialogListener m_listener;


    //
    // Create listener interface to allow activity to create appropriate callback functions.
    //
    public interface ConfigurationDialogListener {
        public void onConfigurationDialogOkay(ConfigurationDialog dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        m_ipAddress = "192.168.1.100";
        m_port = 1024;

        //
        // Create dialog using AlertDialogBuilder
        //
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        //
        // Use custom layout to add the file and format spinners.
        //
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_configuration, null);
        b.setView(view);

        b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //m_listener.onConfigurationDialogOkay(ConfigurationDialog.this);

            }
        });
        //b.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
        //    public void onClick(DialogInterface dialog, int id) {
        //        m_listener.onConfigurationDialogCancel(ConfigurationDialog.this);
        //    }
        //});
        Log.v("DIALOG", "CREATED: ");
        AlertDialog ret = b.create();
        ret.setCanceledOnTouchOutside(false); // do not allow user to cancel by selecting outside

        // save off duration text for later use.
        m_hostText = (EditText) view.findViewById(R.id.ev3_host);
        m_portText = (EditText) view.findViewById(R.id.ev3_port);
        ret.show();

        Button okay_button = ret.getButton(DialogInterface.BUTTON_POSITIVE);
        Log.v("DIALOG", "BUTTON: " + okay_button);
        OkayClickListener l = new OkayClickListener(ret);
        okay_button.setOnClickListener(l);

        //m_hostText.setFilters(new InputFilter[] {new IPAddressInputFilter(getContext())});
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        m_hostText.setText(pm.getString("ip_address", m_hostText.getText().toString()));
        m_portText.setText(pm.getString("port", m_portText.getText().toString()));

        return ret;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.v("DIALOG", "ATTACHED: " );
        //
        // Register listener so that appropriate callback functions are called.
        //
        try {
            m_listener = (ConfigurationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + "ConfigurationDialogListener not implemented!");
        }
    }

    String getEV3Host()
    {
        return m_ipAddress;
    }

    Integer getEV3Port()
    {
        return m_port;
    }

    private ConfigurationDialog outer()
    {
        return this;
    }

    class OkayClickListener implements View.OnClickListener {
        private Dialog m_dialog;

        OkayClickListener(Dialog d) {
            m_dialog = d;
        }

        @Override
        public void onClick(View v) {

            if (!isValidIp()) {
                Toast.makeText(getActivity(), "Invalid IP address entered", Toast.LENGTH_SHORT).show();
            } else if (!isValidPort()) {
                Toast.makeText(getActivity(), "Invalid port entered", Toast.LENGTH_SHORT).show();
            } else {
                m_port = Integer.parseInt(m_portText.getText().toString());
                m_ipAddress =  m_hostText.getText().toString();
                m_dialog.dismiss();
                m_listener.onConfigurationDialogOkay(outer());
            }
        }

        private boolean isValidPort() {
            boolean ret = true;

            try {
                // port must be between 1024-65535
                int port = Integer.parseInt(m_portText.getText().toString());
                if (port < 1024 || port > 65535) {
                    ret =  false;
                }
            } catch (NumberFormatException e) {
                ret =  false;
            }
            return ret;

        }
        private boolean isValidIp() {

            boolean ret = true;

            String ip = m_hostText.getText().toString();
            boolean regex_matches =
                    ip.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");

            if (!regex_matches) {
                ret = false;
            } else {
                String[] octets = ip.split("\\.");

                //verify octests are valid
                for (int i = 0; i < octets.length; i++) {
                    if (!isValidOctet(octets[i])) {
                        ret = false;
                    }
                }
            }
            return ret;
        }

        private boolean isValidOctet(String octet_str) {
            boolean ret = false;
            try {
                int octet = Integer.valueOf(octet_str);
                if (octet <= 255 && octet >= 0) {
                    ret =  true;
                }
            } catch (NumberFormatException e) {
                // pass
            }
            return ret;
        }
    }

}


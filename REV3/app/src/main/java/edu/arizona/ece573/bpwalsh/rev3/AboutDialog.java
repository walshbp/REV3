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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;


//
// AboutDialog - Dialog display information about the REV3 application including author and
//               licensing information.
//
public class AboutDialog extends DialogFragment {


    AboutDialogListener m_listener;


    //
    // Create listener interface to allow activity to create appropriate callback functions.
    //
    public interface AboutDialogListener {
        public void onAboutDialogOkay(AboutDialog dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //
        // Create dialog using AlertDialogBuilder
        //
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        //
        // Use custom layout to add the file and format spinners.
        //
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_about, null);
        b.setView(view);

        b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                m_listener.onAboutDialogOkay(AboutDialog.this);

            }
        });

        Dialog ret = b.create();
        b.setCancelable(false);
        ret.setCanceledOnTouchOutside(false); // do not allow user to cancel by selecting outside

        return ret;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //
        // Register listener so that appropriate callback functions are called.
        //
        try {
            m_listener = (AboutDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + "AboutDialogListener not implemented!");
        }
    }
}


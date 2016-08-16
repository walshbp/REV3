//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
package edu.arizona.ece573.bpwalsh.rev3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

//
// Verify IP address as it is written
//
public class IPAddressInputFilter implements InputFilter {

    private Context m_context;

    public IPAddressInputFilter(Context context)
    {
        m_context = context;
        // nothing to do
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {

        String original_txt = dest.toString();
        String modified_txt = original_txt.substring(0, dstart) + source.subSequence(start, end)
                + original_txt.substring(dend);

        boolean regex_matches =
                modified_txt.matches("^[0-9]{1,3}(\\.([0-9]{1,3}(\\.([0-9]{1,3}(\\.([0-9]{1,3})?)?)?)?)?)?");
        if (end > start) { // adding text
            if (!regex_matches) {
                return ""; // don't let text be added
            } else {
                String[] octets = modified_txt.split("\\.");

                // check octet validity
                for (int i = 0; i < octets.length; i++) {
                    if (!isValidOctet(octets[i])) {
                        return ""; // don't let text be added
                    }
                }
            }
        } else { // deleting text, make sure resulting text is still valid
            if (!regex_matches && !modified_txt.isEmpty()) {
               // ip address is not valid, keep the deleted character

                return dest.subSequence(dstart, dend);
            }
        }
        return null;
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

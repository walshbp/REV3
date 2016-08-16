//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//

package edu.arizona.ece573.bpwalsh.rev3;


import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.InputFilter;
import android.widget.EditText;

public class REV3Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new REV3PreferenceFragment()).commit();
    }

    public static class REV3PreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            EditText hostText = ((EditTextPreference) findPreference("ip_address"))
                    .getEditText();

            hostText.setFilters(new InputFilter[]{new IPAddressInputFilter(getContext())});
        }
    }

}
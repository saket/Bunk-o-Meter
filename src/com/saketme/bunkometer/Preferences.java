package com.saketme.bunkometer;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Preferences extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private static class SettingsFragment extends PreferenceFragment {

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            //Load preferences from an XML file
            addPreferencesFromResource(R.xml.preferences);

        }

    }
}
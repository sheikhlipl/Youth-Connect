package com.lipl.youthconnect.youth_connect.demo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.lipl.youthconnect.youth_connect.R;

/**
 * Created by Android Luminous on 2/21/2016.
 */
public class GrocerySyncPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}

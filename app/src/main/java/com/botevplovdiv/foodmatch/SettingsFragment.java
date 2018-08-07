
package com.botevplovdiv.foodmatch;

import android.preference.PreferenceFragment;
import android.os.Bundle;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences);

    }
}
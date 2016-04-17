package rick.bos.wahooandroid;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by rick on 2016-04-16.
 */
public class ActivitySettings extends Activity {
    @Override
    protected void onCreate(Bundle savedStateInstanceState) {
        super.onCreate(savedStateInstanceState);

        // Display fragment as main content
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);  //load preference screen from xml directory.
        }
    }
}
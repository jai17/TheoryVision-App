package theoryvision.theory6.controls.com.theoryvision;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.util.List;

/**
 * Class that handles the main app settings activity screen and its preferences
 * @author RickHansenRobotics
 * @since 2017-08-16
 */

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_ASSISTANT = "pref_assistant";
    public static final String KEY_WELCOME = "pref_welcome";
    public static final String KEY_NT = "pref_nt";
    public static final String KEY_UDP = "pref_udp";
    public static final String KEY_PROCESSING = "pref_processing";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences_screen);
        }
    }
}

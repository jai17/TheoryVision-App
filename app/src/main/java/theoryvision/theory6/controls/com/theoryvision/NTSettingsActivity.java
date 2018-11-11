package theoryvision.theory6.controls.com.theoryvision;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Class that handles the Networktable settings preferences to output to Roborio
 * @author RickHansenRobotics
 * @since 2017-08-16
 */

public class NTSettingsActivity extends PreferenceActivity {

    public static final String NT_PREF_CONNECTED = "nt_pref_connected";
    public static final String NT_PREF_TRACKED = "nt_pref_targets";
    public static final String NT_PREF_FPS = "nt_pref_fps";
    public static final String NT_PREF_HEIGHT = "nt_pref_height";
    public static final String NT_PREF_WIDTH = "nt_pref_width";
    public static final String NT_PREF_FOV_H = "nt_pref_fov_h";
    public static final String NT_PREF_FOV_V = "nt_pref_fov_v";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new NTFragment())
                .commit();
    }

    public static class NTFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.nt_preferences_screen);
        }
    }
}

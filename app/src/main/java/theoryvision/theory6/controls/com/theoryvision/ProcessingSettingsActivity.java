package theoryvision.theory6.controls.com.theoryvision;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Class that handles the Processing Setting preferences
 * @author RickHansenRobotics
 * @since 2017-12-22
 */

public class ProcessingSettingsActivity extends PreferenceActivity {

    public static final String PROCESSING_PREF_FPS = "fps_prefs";
    public static final String PROCESSING_PREF_RESOLUTION = "resolution_prefs";
    public static final String PROCESSING_PREF_PORTRAIT_ORIENTATION = "portrait_orientation_prefs";
    public static final String PROCESSING_PREF_LANDSCAPE_ORIENTATION = "landscape_orientation_prefs";
    public static final String PROCESSING_PREF_IP = "ip_preference";
    public static final String PROCESSING_PREF_TARGET_WIDTH = "target_width_preference";
    public static final String PROCESSING_PREF_NETWORKTABLES_NAME = "networktables_name_prefs";
    //public static final String PROCESSING_PREF_EXPOSURE = "exposure_prefs";
    public static final String PROCESSING_PREF_ZOOM = "zoom_prefs";
    public static final String PROCESSING_PREF_COLOUR_EFFECT = "colour_effect_prefs";
    public static final String PROCESSING_PREF_WHITE_BALANCE = "white_balance_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new VisualsFragment())
                .commit();
    }

    public static class VisualsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.visuals_preferences_screen);
        }
    }
}

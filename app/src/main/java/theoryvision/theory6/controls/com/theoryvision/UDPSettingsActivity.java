package theoryvision.theory6.controls.com.theoryvision;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Class that handles the UDP output option preferences
 * @author RickHansenRobotics
 * @since 2017-08-16
 */

public class UDPSettingsActivity extends PreferenceActivity {

    public static final String UDP_PREF_ANGLE_H = "udp_pref_angle_h";
    public static final String UDP_PREF_ANGLE_V = "udp_pref_angle_v";
    public static final String UDP_PREF_COORDINATES = "udp_pref_coordinates";
    public static final String UDP_PREF_SYS_TIME = "udp_pref_sys_time";
    public static final String UDP_PREF_DISTANCE_H = "udp_pref_distance_h";
    public static final String UDP_PREF_DISTANCE_V = "udp_pref_distance_v";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new UDPFragment())
                .commit();
    }

    public static class UDPFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.udp_preferences_screen);
        }
    }
}

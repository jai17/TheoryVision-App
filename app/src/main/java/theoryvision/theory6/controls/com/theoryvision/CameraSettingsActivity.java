package theoryvision.theory6.controls.com.theoryvision;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Class in which HSV tuner bar is initialized to have HSV set by User
 * Inputs accessed through UI and saved preferences
 *
 * @author RickHansenRobotics
 * @since 2017-07-28
 */

public class CameraSettingsActivity extends Activity {

    private static final String TAG = "CameraTest";

    private int width;
    private int height;
    private static boolean isRunning = true;
    private static boolean started = false;
    public static DiscreteSeekBar zoomSeekBar;
    public static DiscreteSeekBar exposureSeekBar;
    Spinner whiteBalanceList;
    ArrayAdapter<CharSequence> adapter;
    public static int zoomVal;
    public static int exposureVal;

    private View cameraSettingsPreset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_settings_pop_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        width = dm.widthPixels;
        height = dm.heightPixels;

        getWindow().setLayout(width, (int) (height * 0.33));

        zoomSeekBar = findViewById(R.id.zoom_seekBar);
        exposureSeekBar = findViewById(R.id.exposure_seekBar);


        zoomSeekBar.setProgress(zoomVal);
        exposureSeekBar.setProgress(exposureVal);

        whiteBalanceList = (Spinner) findViewById(R.id.white_balance_Spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.white_balance_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whiteBalanceList.setAdapter(adapter);
        whiteBalanceList.setSelection(CameraView.getCurrentResolutionID());
        whiteBalanceList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        cameraSettingsPreset = (View) findViewById(R.id.camera_settings_preset);

        started = true;

    }

    public void optimalPreset(View view) {
        int optimalExposure = -12;
        int optimalZoom = 0;
        int optimalWhiteBalanceCode = 0;

        zoomSeekBar.setProgress(optimalZoom);
        exposureSeekBar.setProgress(optimalExposure);
        whiteBalanceList.setSelection(0);
    }


    public void onResume() {
        super.onResume();

        Log.d(TAG, "In Resume");
    }

    public void onPause() {
        super.onPause();

        Log.d(TAG, "In Pause");
    }

    public void onStop() {
        super.onStop();
        finish();
        Log.d(TAG, "In Stop");
    }
    public int getZoomProgress(){
        return zoomSeekBar.getProgress();
    }
    public int getExposureProgress(){
        return exposureSeekBar.getProgress();
    }

    public static void setIsRunning(boolean state) {
        isRunning = state;
    }

    public static boolean isPopUp() {
        return isRunning;
    }

    public static boolean hasStarted() {
        return started;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.START | Gravity.BOTTOM;
        //lp.x = 10;
        //lp.y = 100;
        lp.width = width;
        lp.height = (int) (height * 0.4);
        getWindowManager().updateViewLayout(view, lp);
    }
}

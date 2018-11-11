package theoryvision.theory6.controls.com.theoryvision;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import org.florescu.android.rangeseekbar.RangeSeekBar;

/**
 * Class in which HSV tuner bar is initialized to have HSV set by User
 * Inputs accessed through UI and saved preferences
 *
 * @author RickHansenRobotics
 * @since 2017-07-28
 */

public class HSVTunerActivity extends Activity {

    private static final String TAG = "HSVTest";

    private int width;
    private int height;
    private static boolean isRunning = true;
    private static boolean started = false;

    public static int[] curHueRange = new int[]{0, 180};
    public static int[] curSatRange = new int[]{0, 255};
    public static int[] curValRange = new int[]{0, 255};

    public static int hueMin;

    public static RangeSeekBar<Integer> hSeekBar;
    public static RangeSeekBar<Integer> sSeekBar;
    public static RangeSeekBar<Integer> vSeekBar;

    //private View retroPreset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        width = dm.widthPixels;
        height = dm.heightPixels;

        getWindow().setLayout(width, (int) (height * 0.33));

        hSeekBar = findViewById(R.id.h_seek_bar);
        hSeekBar.setRangeValues(0, 180);

        sSeekBar = findViewById(R.id.s_seek_bar);
        sSeekBar.setRangeValues(0, 255);

        vSeekBar = findViewById(R.id.v_seek_bar);
        vSeekBar.setRangeValues(0, 255);

        //retroPreset = (View) findViewById(R.id.retro_preset);

        hueMin = curHueRange[0];

        Bundle HSVData = getIntent().getExtras();
        if (HSVData != null) {
            curHueRange = HSVData.getIntArray("Hue Data");
            curSatRange = HSVData.getIntArray("Sat Data");
            curValRange = HSVData.getIntArray("Val Data");
        }


        hSeekBar.setSelectedMinValue(curHueRange[0]);
        hSeekBar.setSelectedMaxValue(curHueRange[1]);

        sSeekBar.setSelectedMinValue(curSatRange[0]);
        sSeekBar.setSelectedMaxValue(curSatRange[1]);

        vSeekBar.setSelectedMinValue(curValRange[0]);
        vSeekBar.setSelectedMaxValue(curValRange[1]);

        started = true;

        Log.d(TAG, "In Create" + curHueRange[0]);
    }

    /*public void retroPreset(View view) {
        curHueRange[0] = 25;
        curHueRange[1] = 180;
        curSatRange[0] = 0;
        curSatRange[1] = 255;
        curValRange[0] = 72;
        curValRange[1] = 255;

        hSeekBar.setSelectedMinValue(curHueRange[0]);
        hSeekBar.setSelectedMaxValue(curHueRange[1]);
        sSeekBar.setSelectedMinValue(curSatRange[0]);
        sSeekBar.setSelectedMaxValue(curSatRange[1]);
        vSeekBar.setSelectedMinValue(curValRange[0]);
        vSeekBar.setSelectedMaxValue(curValRange[1]);
    }*/

    public static void setCurHueRange(int min, int max) {
        curHueRange[0] = min;
        curHueRange[1] = max;
    }

    public static void setCurSatRange(int min, int max) {
        curSatRange[0] = min;
        curSatRange[1] = max;
    }

    public static void setCurValRange(int min, int max) {
        curValRange[0] = min;
        curValRange[1] = max;
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

    public static int[] getHueRange() {
        return new int[]{hSeekBar.getSelectedMinValue(), hSeekBar.getSelectedMaxValue()};
    }

    public static int[] getSatRange() {
        return new int[]{sSeekBar.getSelectedMinValue(), sSeekBar.getSelectedMaxValue()};
    }

    public static int[] getValRange() {
        return new int[]{vSeekBar.getSelectedMinValue(), vSeekBar.getSelectedMaxValue()};
    }

    public int getHueMin() {
        return curHueRange[0];
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
        lp.height = (int) (height * 0.33);
        getWindowManager().updateViewLayout(view, lp);
    }
}

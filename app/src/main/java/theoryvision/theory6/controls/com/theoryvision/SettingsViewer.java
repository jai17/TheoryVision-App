package theoryvision.theory6.controls.com.theoryvision;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import org.opencv.android.JavaCameraView;

/**
 * Class that handles the save and load preferences for vision settings (HSV, Filters etc.)
 * @author RickHansenRobotics
 * @since 2017-08-16
 */

public class SettingsViewer extends Activity{

    public static String name;
    TextView title;
    TextView hue;
    TextView sat;
    TextView val;
    TextView boundRects;
    TextView area;
    TextView perimeter;
    TextView widthT;
    TextView heightT;
    TextView solidity;
    TextView vertices;
    TextView ratio;
    TextView resolution;
    TextView FPS;
    TextView pOrientation;
    TextView lOrientation;
    TextView whiteBalance;
    TextView zoom;
    TextView robotIP;
    TextView networktable;
    TextView targetWidth;
    TextView colourEffect;
    TextView hFOV;
    TextView vFOV;
    TextView hFocal;
    TextView vFocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_viewer_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.85), (int)(height*.75));

        title = (TextView)findViewById(R.id.viewer_title);
        title.setText("Settings Viewer");
        hue = (TextView)findViewById(R.id.h_maxmin_title);
        hue.setText("Hue Range is "+CameraView.curHueRange[0]+"-"+CameraView.curHueRange[1]);
        sat = (TextView)findViewById(R.id.s_maxmin_title);
        sat.setText("Saturation Range is "+CameraView.curSatRange[0]+"-"+CameraView.curSatRange[1]);
        val = (TextView)findViewById(R.id.v_maxmin_title);
        val.setText("Value Range is "+CameraView.curValRange[0]+"-"+CameraView.curValRange[1]);
        boundRects = (TextView)findViewById(R.id.bound_rect_title);
        boundRects.setText("Number of Targets is "+VisionPipeline.boundRects);
        area = (TextView)findViewById(R.id.area_maxmin_title);
        area.setText("Area Range is " +VisionPipeline.curSettings[0][0]+"-"+VisionPipeline.currentSettings()[0][1]);
        perimeter = (TextView)findViewById(R.id.perimeter_maxmin_title);
        perimeter.setText("Perimeter Range is " +VisionPipeline.curSettings[1][0]+"-"+VisionPipeline.currentSettings()[1][1]);
        widthT = (TextView)findViewById(R.id.width_maxmin_title);
        widthT.setText("Width Range is " +VisionPipeline.curSettings[2][0]+"-"+VisionPipeline.currentSettings()[2][1]);
        heightT = (TextView)findViewById(R.id.height_maxmin_title);
        heightT.setText("Height Range is " +VisionPipeline.curSettings[3][0]+"-"+VisionPipeline.currentSettings()[3][1]);
        solidity = (TextView)findViewById(R.id.solidity_maxmin_title);
        solidity.setText("Solidity Range is " +VisionPipeline.curSettings[4][0]+"-"+VisionPipeline.currentSettings()[4][1]);
        vertices = (TextView)findViewById(R.id.vertices_maxmin_title);
        vertices.setText("Vertices Range is " +VisionPipeline.curSettings[5][0]+"-"+VisionPipeline.currentSettings()[5][1]);
        ratio = (TextView)findViewById(R.id.ratio_maxmin_title);
        ratio.setText("Ratio Range is " +VisionPipeline.curSettings[6][0]+"-"+VisionPipeline.currentSettings()[6][1]);
        resolution = (TextView)findViewById(R.id.resolution_title);
        resolution.setText("Resolution is " +CameraView.getWidth()+"x"+CameraView.getHeight());
        FPS = (TextView)findViewById(R.id.fps_title);
        FPS.setText("Frames per Second: " + JavaCameraView.manualFPS/1000+"FPS");
        pOrientation = (TextView)findViewById(R.id.portrait_orientation_title);
        if(CameraView.portrait180){
            pOrientation.setText("Portrait Orientation is Flipped 180");
        }else{
            pOrientation.setText("Portrait Orientation is Regular");
        }
        lOrientation = (TextView)findViewById(R.id.landscape_orientation_title);
        if(CameraView.landscape180){
            lOrientation.setText("Landscape Orientation is Flipped 180");
        }else{
            lOrientation.setText("Landscape Orientation is Regular");
        }
        whiteBalance = (TextView)findViewById(R.id.white_balance_title);
        int whiteBalanceCode = JavaCameraView.whiteBalanceCode;
        if(whiteBalanceCode == 0){
            whiteBalance.setText("White Balance is set to Daylight");
        }
        else if (whiteBalanceCode == 1){
            whiteBalance.setText("White Balance is set to Fluorescent");
        }
        else if (whiteBalanceCode == 2){
            whiteBalance.setText("White Balance is set to Warm Fluorescent");
        }
        else if (whiteBalanceCode == 3){
            whiteBalance.setText("White Balance is set to Auto");
        }
        else if (whiteBalanceCode == 4){
            whiteBalance.setText("White Balance is set to Cloudy Daylight");
        }
        else if (whiteBalanceCode == 5){
            whiteBalance.setText("White Balance is set to Incandescent");
        }
        else if (whiteBalanceCode == 6){
            whiteBalance.setText("White Balance is set to Shade");
        }
        else if (whiteBalanceCode == 7){
            whiteBalance.setText("White Balance is set to Twilight");
        }
        zoom = (TextView)findViewById(R.id.zoom_title);
        zoom.setText("Zoom is set to "+JavaCameraView.zoomVal+"X");
        robotIP = (TextView)findViewById(R.id.robot_ip_title) ;
        robotIP.setText("Robot IP Address is set to "+Networking.ROBORIO_ADDRESS);
        networktable = (TextView)findViewById(R.id.networktable_title);
        networktable.setText("Networktable Name is set to "+"'"+CameraView.networktableName+"'");
        targetWidth = (TextView)findViewById(R.id.target_width_title);
        targetWidth.setText("Target Width is set to "+CameraView.targetWidth+" Inches");
        colourEffect = (TextView)findViewById(R.id.colour_effect_title);
        int colourEffectCode = JavaCameraView.colourEffectCode;
        if(colourEffectCode == 0){
            colourEffect.setText("Colour Effect is set to None");
        }
        else if (colourEffectCode == 1){
            colourEffect.setText("Colour Effect is set to Aqua");
        }
        else if (colourEffectCode == 2){
            colourEffect.setText("Colour Effect is set to Blackboard");
        }
        else if (colourEffectCode == 3){
            colourEffect.setText("Colour Effect is set to Mono");
        }
        else if (colourEffectCode == 4){
            colourEffect.setText("Colour Effect is set to Negative");
        }
        else if (colourEffectCode == 5){
            colourEffect.setText("Colour Effect is set to Posterize");
        }
        else if (colourEffectCode == 6){
            colourEffect.setText("Colour Effect is set to Sepia");
        }
        else if (colourEffectCode == 7){
            colourEffect.setText("Colour Effect is set to Solarize");
        }
        else if (colourEffectCode == 8){
            colourEffect.setText("Colour Effect is set to Whiteboard");
        }
        hFOV = (TextView)findViewById(R.id.fov_horizontal_title);
        hFOV.setText("The Horizontal Field of View is: "+CameraView.getFieldOfViewHorizontal());
        vFOV = (TextView)findViewById(R.id.fov_vertical_title);
        vFOV.setText("The Vertical Field of View is: "+CameraView.getFieldOfViewVertical());
        hFocal = (TextView)findViewById(R.id.focal_horizontal_title);
        hFocal.setText("The Horizontal Focal Length is: "+CameraView.getFocalLengthHorizontal());
        vFocal = (TextView)findViewById(R.id.focal_verticle_title);
        vFocal.setText("The Vertical Focal Length is: "+CameraView.getFocalLengthVertical());

        /*TextView textView2 = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
        textView2.setLayoutParams(layoutParams);
        textView2.setText("programmatically created TextView2");
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView2.setBackgroundColor(0xffffdbdb); // hex color 0xAARRGGBB
        linearLayout.addView(textView2);*/

    }

}

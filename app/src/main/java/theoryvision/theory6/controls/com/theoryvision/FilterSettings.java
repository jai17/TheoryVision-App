package theoryvision.theory6.controls.com.theoryvision;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.florescu.android.rangeseekbar.RangeSeekBar;

/**
 *
 * Class in which Filters are initialized and set through the UI
 * Inputs accesed through UI and saved preferences
 *
 * @author RickHansenRobotics
 * @since 2017-07-28
 *
 */

public class FilterSettings extends Activity {

    private static final String TAG = "FilterSettings";

    CameraView cameraView;

    EditText boundRects;
    private EditText empty;

    public static RangeSeekBar<Double> minMaxArea;
    public static RangeSeekBar<Double> minMaxPerimeter;
    public static RangeSeekBar<Double> minMaxWidth;
    public static RangeSeekBar<Double> minMaxHeight;
    public static RangeSeekBar<Double> minMaxSolidity;
    public static RangeSeekBar<Double> minMaxVertices;
    public static RangeSeekBar<Double> minMaxRatio;

    private static double[][] curSettings;
    private static double[] filterContoursMinArea = {4000, 5000};
    private static double[] filterContoursMinPerimeter = {100, 1000};
    private static double[] filterContoursWidth = {100, 1000};
    private static double[] filterContoursHeight = {100, 1000};
    private static double[] filterContoursSolidity = {10, 100};
    private static double[] filterContoursVertices = {1000, 1000000};
    private static double[] filterContoursRatio = {100, 1000};
    private static double boundingRects = 4.0;
    private static double unused = 0.0;

    //private View filterPreset;

    private Button saveButton;

    Spinner resolutionList;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filter_settings);

        boundRects = findViewById(R.id.bound_rects);
        boundRects.setHint("# of Targets (ex.2)");
        boundRects.setText(""+(int)VisionPipeline.currentSettings()[7][0]);

        empty = findViewById(R.id.empty);
        empty.setHint("Team Number");
        empty.setText(""+VisionPipeline.currentSettings()[7][1]);

        minMaxArea = findViewById(R.id.min_max_area);
        minMaxPerimeter = findViewById(R.id.min_max_perimeter);
        minMaxWidth = findViewById(R.id.min_max_width);
        minMaxHeight = findViewById(R.id.min_max_height);
        minMaxSolidity = findViewById(R.id.min_max_solidity);
        minMaxVertices = findViewById(R.id.min_max_vertices);
        minMaxRatio = findViewById(R.id.min_max_ratio);

        //filterPreset = (View) findViewById(R.id.filters_preset);

        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(
               new View.OnClickListener(){
                   public void onClick(View v){
                       buttonClicked(v);
                   }
               }
        );

        minMaxArea.setRangeValues(0.0, 5000.0);
        minMaxPerimeter.setRangeValues(0.0, 1000.0);
        minMaxWidth.setRangeValues(0.0, 1000.0);
        minMaxHeight.setRangeValues(0.0, 1000.0);
        minMaxSolidity.setRangeValues(0.0, 100.0);
        minMaxVertices.setRangeValues(0.0, 1000000.0);
        minMaxRatio.setRangeValues(0.0, 1000.0);

        minMaxArea.setSelectedMinValue(VisionPipeline.currentSettings()[0][0]);
        minMaxArea.setSelectedMaxValue(VisionPipeline.currentSettings()[0][1]);

        minMaxPerimeter.setSelectedMinValue(VisionPipeline.currentSettings()[1][0]);
        minMaxPerimeter.setSelectedMaxValue(VisionPipeline.currentSettings()[1][1]);

        minMaxWidth.setSelectedMinValue(VisionPipeline.currentSettings()[2][0]);
        minMaxWidth.setSelectedMaxValue(VisionPipeline.currentSettings()[2][1]);

        minMaxHeight.setSelectedMinValue(VisionPipeline.currentSettings()[3][0]);
        minMaxHeight.setSelectedMaxValue(VisionPipeline.currentSettings()[3][1]);

        minMaxSolidity.setSelectedMinValue(VisionPipeline.currentSettings()[4][0]);
        minMaxSolidity.setSelectedMaxValue(VisionPipeline.currentSettings()[4][1]);

        minMaxVertices.setSelectedMinValue(VisionPipeline.currentSettings()[5][0]);
        minMaxVertices.setSelectedMaxValue(VisionPipeline.currentSettings()[5][1]);

        minMaxRatio.setSelectedMinValue(VisionPipeline.currentSettings()[6][0]);
        minMaxRatio.setSelectedMaxValue(VisionPipeline.currentSettings()[6][1]);

        /*resolutionList = (Spinner) findViewById(R.id.resolution_list);
        adapter = ArrayAdapter.createFromResource(this, R.array.resolution_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resolutionList.setAdapter(adapter);
        resolutionList.setSelection(CameraView.getCurrentResolutionID());
        resolutionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        CameraView.changeResolution(0, 1280, 720);
                        break;
                    case 1:
                        CameraView.changeResolution(1, 720, 480);
                        break;
                    case 2:
                        CameraView.changeResolution(2, 640, 480);
                        break;
                    case 3:
                        CameraView.changeResolution(3, 320, 240);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "In pause");
    }

    public void onStop(){
        super.onStop();
        Log.d(TAG, "In Stop");
    }

    public void buttonClicked(View v){
        VisionPipeline.changeSettings(getSettings());
        Intent launchActivity = new Intent(this, CameraView.class);
        launchActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(launchActivity);
        finish();
    }

    public double[][] getSettings(){
        double[][] settings = new double[][]{areaRange(),
                                        perimeterRange(),
                                        widthRange(),
                                        heightRange(),
                                        solidityRange(),
                                        verticesRange(),
                                        ratioRange(),
                                        {boundRects(), empty()}};

        return settings;
    }
    public double[][] getPresetSettings(){
        double[][] settings = new double[][]{filterContoursMinArea,
                                             filterContoursMinPerimeter,
                                             filterContoursWidth,
                                             filterContoursHeight,
                                             filterContoursSolidity,
                                             filterContoursVertices,
                                             filterContoursRatio,
                                             {boundingRects, unused}};

        return settings;
    }

/*
    public void retroPreset(View view){
        this.changeSettings(getPresetSettings());
        boundRects.setText(""+boundingRects);
        empty.setText(""+unused);

        minMaxArea.setSelectedMinValue(filterContoursMinArea[0]);
        minMaxArea.setSelectedMaxValue(filterContoursMinArea[1]);

        minMaxPerimeter.setSelectedMinValue(filterContoursMinPerimeter[0]);
        minMaxPerimeter.setSelectedMaxValue(filterContoursMinPerimeter[1]);

        minMaxWidth.setSelectedMinValue(filterContoursWidth[0]);
        minMaxWidth.setSelectedMaxValue(filterContoursWidth[1]);

        minMaxHeight.setSelectedMinValue(filterContoursHeight[0]);
        minMaxHeight.setSelectedMaxValue(filterContoursHeight[1]);

        minMaxSolidity.setSelectedMinValue(filterContoursSolidity[0]);
        minMaxSolidity.setSelectedMaxValue(filterContoursSolidity[1]);

        minMaxVertices.setSelectedMinValue(filterContoursVertices[0]);
        minMaxVertices.setSelectedMaxValue(filterContoursVertices[1]);

        minMaxRatio.setSelectedMinValue(filterContoursRatio[0]);
        minMaxRatio.setSelectedMaxValue(filterContoursRatio[1]);
    }
*/

    public static void changeSettings(double[][] settings){
        filterContoursMinArea = settings[0];
        filterContoursMinPerimeter = settings[1];
        filterContoursWidth = settings[2];
        filterContoursHeight = settings[3];
        filterContoursSolidity = settings[4];
        filterContoursVertices = settings[5];
        filterContoursRatio = settings[6];
        boundingRects = settings[7][0];
        unused = settings[7][1];

        curSettings = settings;
    }

    public static void setNumBoundingRects(int num){
        boundingRects = num;
    }

    public static double[][] currentSettings(){
        return curSettings;
    }

    public double[] areaRange(){
        return new double[]{minMaxArea.getSelectedMinValue(), minMaxArea.getSelectedMaxValue()};
    }

    public double[] perimeterRange(){
        return new double[]{minMaxPerimeter.getSelectedMinValue(), minMaxPerimeter.getSelectedMaxValue()};
    }

    public double[] widthRange(){
        return new double[]{minMaxWidth.getSelectedMinValue(), minMaxWidth.getSelectedMaxValue()};
    }

    public double[] heightRange(){
        return new double[]{(double) minMaxHeight.getSelectedMinValue(), (double)minMaxHeight.getSelectedMaxValue()};
    }

    public double[] solidityRange(){
        return new double[]{(double) minMaxSolidity.getSelectedMinValue(), (double)minMaxSolidity.getSelectedMaxValue()};
    }

    public double[] verticesRange(){
        return new double[]{(double) minMaxVertices.getSelectedMinValue(), (double) minMaxVertices.getSelectedMaxValue()};
    }

    public double[] ratioRange(){
        return new double[]{(double) minMaxRatio.getSelectedMinValue(), (double)minMaxRatio.getSelectedMaxValue()};
    }

    public double boundRects(){
        if(!boundRects.getText().toString().contentEquals(""))
            return Double.parseDouble(boundRects.getText().toString());
        else
            return VisionPipeline.BOUND_RECTS;
    }
    public double empty(){
        if(!empty.getText().toString().contentEquals(""))
            return Double.parseDouble(empty.getText().toString());
        else
            return VisionPipeline.EMPTY;
    }
}

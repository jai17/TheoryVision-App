package theoryvision.theory6.controls.com.theoryvision;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import static android.R.color.white;
import static theoryvision.theory6.controls.com.theoryvision.R.color.brightBlue;
import static theoryvision.theory6.controls.com.theoryvision.R.color.colorOrange;

/**
 * Class that handles the save and load preferences for vision settings (HSV, Filters etc.)
 * @author RickHansenRobotics
 * @since 2017-08-16
 */

public class SaveSettings extends Activity{

    public static LinearLayout layout;
    public static String name;
    public static Set<String> emailSet;

    public static boolean setAsDefaultSettings;

    public static Set<String> fileNameSet;

    public static SharedPreferences save;
    public static SharedPreferences.Editor editor3;
    private static final String[] FILENAMES = new String[] {
            "Comp1", "Comp2", "Comp3", "Worlds","Einstein", "Practice Field", "School"
    };
    private int HMin, HMax, SMin, SMax, VMin, VMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.save_pop_windows);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width), (int)(height*0.3));

        emailSet = new HashSet<String>();
        fileNameSet = new HashSet<String>();

        save = getSharedPreferences("save", MODE_PRIVATE);

        if(save.getString("fileName", null) != null){
            emailSet.add(save.getString("fileName", "hool"));
        }
        editor3 = save.edit();
        editor3.putStringSet("fileNames", emailSet);
        editor3.apply();

        fileNameSet = save.getStringSet("fileNames", null);


       /* AutoCompleteTextView saveName = (AutoCompleteTextView)findViewById(R.id.settings_name);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, FILENAMES);
        //saveName.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(save.getStringSet("fileNames", emailSet))));
        saveName.setAdapter(adapter);
        saveName.setThreshold(1);
        saveName.canScrollVertically(1);
        

        saveName.setHint("Name of File");
        */

       Spinner fileDropDown = findViewById(R.id.fileSpinner);
        ArrayAdapter<String> fileAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, FILENAMES);
        fileDropDown.setAdapter(fileAdapter);
        fileDropDown.setBackgroundColor(getResources().getColor(white));
        fileDropDown.setPrompt("Prompt");

        Switch defaultSwitch = findViewById(R.id.defaultSwitch);


        name = fileDropDown.getSelectedItem().toString();

       /* EditText saveName = (EditText) findViewById(R.id.settings_name);
        saveName.setHint("Name Of File");
        name = saveName.getText().toString();*/

        /*Toast.makeText(this,
                saveName.getText().toString(),
                Toast.LENGTH_SHORT).show();
*/
        Button save = (Button) findViewById(R.id.s_settings);
        Button load = (Button) findViewById(R.id.l_settings);

        save.setBackgroundColor(getResources().getColor(colorOrange));
        load.setBackgroundColor(getResources().getColor(brightBlue));

        defaultSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAsDefaultSettings = true;
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //name = saveName.getText().toString();
                name = fileDropDown.getSelectedItem().toString();
                saveAs(v, name);
                if (setAsDefaultSettings){
                    setAsDefault();
                }
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //name = saveName.getText().toString();
                name = fileDropDown.getSelectedItem().toString();
                load(v, name);
            }
        });
    }
    public void saveAs (View v, String name){
        Toast.makeText(this,
                name,
                Toast.LENGTH_SHORT).show();

        SharedPreferences saveSettings = getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = saveSettings.edit();

        //Saving # of Bounding Rectangles
        editor.putInt("savedBoundRect", (int) VisionPipeline.boundRects);
        Log.d("listSetting", "" + VisionPipeline.boundRects);
        //Saving H,S,V Settings
        editor.putInt("savedHMin", CameraView.hueMin);
        Log.d("Setting", "" + CameraView.hueMin);
        editor.putInt("savedHMax", CameraView.hueMax);
        editor.putInt("savedSMin", CameraView.satMin);
        editor.putInt("savedSMax", CameraView.satMax);
        editor.putInt("savedVMin", CameraView.valMin);
        editor.putInt("savedVMax", CameraView.valMax);

        //Saving Filter Settings
        editor.putInt("savedMinArea", (int) VisionPipeline.currentSettings()[0][0]);
        editor.putInt("savedMinPerimeter", (int) VisionPipeline.currentSettings()[1][0]);
        editor.putInt("savedMinWidth", (int) VisionPipeline.currentSettings()[2][0]);
        editor.putInt("savedMinHeight", (int) VisionPipeline.currentSettings()[3][0]);
        editor.putInt("savedMinSolidity", (int) VisionPipeline.currentSettings()[4][0]);
        editor.putInt("savedMinVertices", (int) VisionPipeline.currentSettings()[5][0]);
        editor.putInt("savedMinRatio", (int) VisionPipeline.currentSettings()[6][0]);
        editor.putInt("savedMaxArea", (int) VisionPipeline.currentSettings()[0][1]);
        editor.putInt("savedMaxPerimeter", (int) VisionPipeline.currentSettings()[1][1]);
        editor.putInt("savedMaxWidth", (int) VisionPipeline.currentSettings()[2][1]);
        editor.putInt("savedMaxHeight", (int) VisionPipeline.currentSettings()[3][1]);
        editor.putInt("savedMaxSolidity", (int) VisionPipeline.currentSettings()[4][1]);
        editor.putInt("savedMaxVertices", (int) VisionPipeline.currentSettings()[5][1]);
        editor.putInt("savedMaxRatio", (int) VisionPipeline.currentSettings()[6][1]);
        //Saving Resolution Settings
        editor.putInt("savedResolutionID", CameraView.currentResolutionID);
        editor.putInt("savedResolutionWidth", CameraView.getWidth());
        editor.putInt("savedResolutionHeight", CameraView.getHeight());

        editor.putString("fileName", name);

        String fileName = (name);

        emailSet.add(fileName);
        editor3.putString("fileName", fileName);
        editor3.putStringSet("fileNames", emailSet);
        editor3.apply();

        editor.apply();
        //editor.commit();



        Snackbar.make(this.findViewById(android.R.id.content), "Settings Saved Succesfully", Snackbar.LENGTH_LONG).show();

        Toast.makeText(this,
                "Settings Saved Succesfully",
                Toast.LENGTH_SHORT).show();

        Intent launchActivity = new Intent(this, CameraView.class);
        launchActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(launchActivity);
        finish();

    }
    public void load (View v, String name){
        Toast.makeText(this,
                name,
                Toast.LENGTH_SHORT).show();

        SharedPreferences loadSettings = getSharedPreferences(name, MODE_PRIVATE);
        //Loading # of Bounding Rectangles
        int boundR = loadSettings.getInt("savedBoundRect", VisionPipeline.BOUND_RECTS);
        VisionPipeline.boundRects = boundR;

        //Loading Resolution Settings
        int ID = loadSettings.getInt("savedResolutionID", 0);
        int mWidth = loadSettings.getInt("savedResolutionWidth", 640);
        int mHeight = loadSettings.getInt("savedResolutionHeight", 480);
        CameraView.changeResolution(ID, mWidth, mHeight);

        //Loading H,S,V Settings
         HMin = loadSettings.getInt("savedHMin", 0);
         HMax = loadSettings.getInt("savedHMax", 180);
        Log.d("HueMinSaved", "" + HMin);

         SMin = loadSettings.getInt("savedSMin", 0);
         SMax = loadSettings.getInt("savedSMax", 255);

         VMin = loadSettings.getInt("savedVMin", 0);
         VMax = loadSettings.getInt("savedVMax", 255);

        //Sending HSV data to CameraView class to be used
        int [] Hue = new int [] {HMin, HMax};
        int [] Sat = new int [] {SMin, SMax};
        int [] Val = new int [] {VMin, VMax};
        Intent NewActivity = new Intent(SaveSettings.this, CameraView.class);
        NewActivity.putExtra("hue", Hue);
        NewActivity.putExtra("sat", Sat);
        NewActivity.putExtra("val", Val);
        startActivity(NewActivity);

        //Loading Filter Settings
        //*Load Min/Max Area**//*
        int minArea = loadSettings.getInt("savedMinArea", (int) VisionPipeline.MIN_AREA);
        VisionPipeline.filterContoursMinArea[0] = minArea;
        Log.d("loadedSetting", "" + minArea);
        int maxArea = loadSettings.getInt("savedMaxArea", 5000);
        VisionPipeline.filterContoursMinArea[1] = maxArea;

        //*Load Min/Max Perimeter**//*
        int minPerimeter = loadSettings.getInt("savedMinPerimeter", (int) VisionPipeline.MIN_PERIMETER);
        VisionPipeline.filterContoursMinPerimeter[0] = minPerimeter;

        int maxPerimeter = loadSettings.getInt("savedMaxPerimeter", 1000);
        VisionPipeline.filterContoursMinPerimeter[1] = maxPerimeter;

        //*Load Min/Max Width**//*
        int minWidth = loadSettings.getInt("savedMinWidth", 0);
        VisionPipeline.filterContoursWidth[0] = minWidth;

        int maxWidth = loadSettings.getInt("savedMaxWidth", 1000);
        VisionPipeline.filterContoursWidth[1] = maxWidth;

        //*Load Min/Max Height**//*
        int minHeight = loadSettings.getInt("savedMinHeight", 0);
        VisionPipeline.filterContoursHeight[0] = minHeight;

        int maxHeight = loadSettings.getInt("savedMaxHeight", 1000);
        VisionPipeline.filterContoursHeight[1] = maxHeight;

        //*Load Min/Max Solidity**//*
        int minSolidity = loadSettings.getInt("savedMinSolidity", 0);
        VisionPipeline.filterContoursSolidity[0] = minSolidity;

        int maxSolidity = loadSettings.getInt("savedMaxSolidity", 100);
        VisionPipeline.filterContoursSolidity[1] = maxSolidity;

        //*Load Min/Max Vertices**//*
        int minVertices = loadSettings.getInt("savedMinVertices", 0);
        VisionPipeline.filterContoursVertices[0] = minVertices;

        int maxVertices = loadSettings.getInt("savedMaxVertices", 1000000);
        VisionPipeline.filterContoursVertices[1] = maxVertices;

        //*Load Min/Max Ratio**//*
        int minRatio = loadSettings.getInt("savedMinRatio", 0);
        VisionPipeline.filterContoursRatio[0] = minRatio;

        int maxRatio = loadSettings.getInt("savedMaxRatio", 1000);
        VisionPipeline.filterContoursRatio[1] = maxRatio;

        //////////////////
       String fileName = loadSettings.getString("fileName", null);
        editor3.putString("fileName", fileName);
        editor3.apply();
        editor3.commit();
        //emailSet.add(fileName);

        Toast.makeText(this,
                "Settings Loaded Succesfully",
                Toast.LENGTH_SHORT).show();

        Snackbar.make(this.findViewById(android.R.id.content), "Settings Loaded Succesfully", Snackbar.LENGTH_LONG).show();

        Intent launchActivity = new Intent(this, CameraView.class);
        launchActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(launchActivity);
        CameraView.loadDefaultOrLoaded = false;

        finish();
    }

    public void setAsDefault(){

        SharedPreferences defaultSettingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSettingsPrefs.edit();

        //Saving # of Bounding Rectangles
        editor.putInt("savedBoundRect", (int) VisionPipeline.boundRects);
        Log.d("listSetting", "" + VisionPipeline.boundRects);
        //Saving H,S,V Settings
        editor.putInt("savedHMin", CameraView.hueMin);
        Log.d("Setting", "" + CameraView.hueMin);
        editor.putInt("savedHMax", CameraView.hueMax);
        editor.putInt("savedSMin", CameraView.satMin);
        editor.putInt("savedSMax", CameraView.satMax);
        editor.putInt("savedVMin", CameraView.valMin);
        editor.putInt("savedVMax", CameraView.valMax);

        //Saving Filter Settings
        editor.putInt("savedMinArea", (int) VisionPipeline.currentSettings()[0][0]);
        editor.putInt("savedMinPerimeter", (int) VisionPipeline.currentSettings()[1][0]);
        editor.putInt("savedMinWidth", (int) VisionPipeline.currentSettings()[2][0]);
        editor.putInt("savedMinHeight", (int) VisionPipeline.currentSettings()[3][0]);
        editor.putInt("savedMinSolidity", (int) VisionPipeline.currentSettings()[4][0]);
        editor.putInt("savedMinVertices", (int) VisionPipeline.currentSettings()[5][0]);
        editor.putInt("savedMinRatio", (int) VisionPipeline.currentSettings()[6][0]);
        editor.putInt("savedMaxArea", (int) VisionPipeline.currentSettings()[0][1]);
        editor.putInt("savedMaxPerimeter", (int) VisionPipeline.currentSettings()[1][1]);
        editor.putInt("savedMaxWidth", (int) VisionPipeline.currentSettings()[2][1]);
        editor.putInt("savedMaxHeight", (int) VisionPipeline.currentSettings()[3][1]);
        editor.putInt("savedMaxSolidity", (int) VisionPipeline.currentSettings()[4][1]);
        editor.putInt("savedMaxVertices", (int) VisionPipeline.currentSettings()[5][1]);
        editor.putInt("savedMaxRatio", (int) VisionPipeline.currentSettings()[6][1]);
        //Saving Resolution Settings
        editor.putInt("savedResolutionID", CameraView.currentResolutionID);
        editor.putInt("savedResolutionWidth", CameraView.getWidth());
        editor.putInt("savedResolutionHeight", CameraView.getHeight());


        editor.putString("fileName", "defaultIsThere");

        String fileName = (name);

        editor.apply();
        //editor.commit();



        Snackbar.make(this.findViewById(android.R.id.content), "Settings Saved Succesfully", Snackbar.LENGTH_LONG).show();

        Toast.makeText(this,
                "Settings Saved Succesfully",
                Toast.LENGTH_SHORT).show();

        Intent launchActivity = new Intent(this, CameraView.class);
        launchActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(launchActivity);
        finish();
    }
}

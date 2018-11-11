package theoryvision.theory6.controls.com.theoryvision;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.FpsMeter;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Main Activity class for TheoryVision that handles:
 * All sub-classes ({@link FilterSettings}, {@link HSVTunerActivity},
 * {@link Networking}, {@link NTSettingsActivity},
 * {@link SaveSettings}, {@link SettingsActivity},
 * {@link ShadowOptions}, {@link TargetData},
 * {@link Targets}, {@link UDPSettingsActivity},
 * {@link VisionPipeline}, {@link VoiceRecognizer},
 * {@link WelcomeScreen})
 * OpenCV (loading, troubleshooting and version control)
 * Camera outputs and processing (onCameraFrame)
 * Action Buttons (FAB)
 * Save/Load Menu (onCreateOptionsMenu)
 * Side Menu (App Settings, HSV, Filter Settings, Preview Image, Output Image, Connections)
 *
 * @author RickHansenRobotics
 * @since 2017-07-28
 */

public class CameraView extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "OpenCVTest";

    private static final int VIEW_MODE_RGB = 0;
    private static final int VIEW_MODE_HSV = 1;
    private static final int VIEW_MODE_CONTOURS = 2;
    private static final int VIEW_MODE_OUTPUT = 3;

    private int mViewMode = 0;
    public static int currentResolutionID = 0;
    private boolean connectedNT = false;
    private boolean connectedUDP = false;
    public static double fieldOfView;
    static JavaCameraView cameraView;
    VisionPipeline pipeline;
    Targets targets;
    Mat mRgba;
    Mat mHsv;
    Mat mContour;
    Mat mOutput;
    FloatingActionMenu actionMenu;
    FloatingActionButton previewAction;
    FloatingActionButton hsvAction;
    FloatingActionButton contourAction;
    FloatingActionButton outputAction;
    FloatingActionButton voiceAction;
    public static int[] curHueRange = new int[]{0, 180};
    public static int[] curSatRange = new int[]{0, 255};
    public static int[] curValRange = new int[]{0, 255};
    MenuItem mSaveOptions;
    MenuItem mSettingsViewer;
    MenuItem networkTablesIcon;
    MenuItem udpIcon;
    MenuItem trackingStatus;
    public static int hueMin, hueMax, satMin, satMax, valMin, valMax;
    public static boolean loadDefaultOrLoaded = true;
    public static boolean trackingStatusBoolean = false;
    public static boolean portrait180 = false;
    public static boolean landscape180 = false;
    public static double targetWidth = 10;
    public static String networktableName = "SmartDashboard";

    FragmentManager fm;
    VoiceRecognizer listenerFragment;

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(CameraView.this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    cameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fm = getFragmentManager();

        listenerFragment = (VoiceRecognizer) fm.findFragmentByTag("speech");

        if (listenerFragment == null) {
            listenerFragment = new VoiceRecognizer();
            // Tell it who it is working with.
            fm.beginTransaction().add(listenerFragment, "speech").commit();
        }

        if (loadDefaultOrLoaded){
            setDefaultSettings();
        }

        actionMenu = (FloatingActionMenu) findViewById(R.id.floating_menu);
        actionMenu.setClosedOnTouchOutside(true);
        actionMenu.setMenuButtonColorPressed(R.color.colorOrange);
        actionMenu.setMenuButtonColorRipple(R.color.colorOrange);

        previewAction = (FloatingActionButton) findViewById(R.id.preview);
        previewAction.setButtonSize(FloatingActionButton.SIZE_MINI);
        previewAction.setLabelText("Preview");
        previewAction.setLabelVisibility(View.VISIBLE);
        previewAction.setImageResource(R.drawable.camera);
        previewAction.setColorNormalResId(R.color.brightBlue);
        previewAction.setColorRipple(getColor(R.color.colorOrange));

        hsvAction = (FloatingActionButton) findViewById(R.id.hsv_view_action);
        hsvAction.setButtonSize(FloatingActionButton.SIZE_MINI);
        hsvAction.setLabelText("HSV");
        hsvAction.setLabelVisibility(View.VISIBLE);
        hsvAction.setImageResource(R.drawable.rgb);
        hsvAction.setColorNormalResId(R.color.brightBlue);
        hsvAction.setColorRipple(getColor(R.color.colorOrange));

        contourAction = (FloatingActionButton) findViewById(R.id.contour_view_action);
        contourAction.setButtonSize(FloatingActionButton.SIZE_MINI);
        contourAction.setLabelText("Contour");
        contourAction.setLabelVisibility(View.VISIBLE);
        contourAction.setImageResource(R.drawable.filter);
        contourAction.setColorNormalResId(R.color.brightBlue);
        contourAction.setColorRipple(getColor(R.color.colorOrange));

        outputAction = (FloatingActionButton) findViewById(R.id.output_view_action);
        outputAction.setButtonSize(FloatingActionButton.SIZE_MINI);
        outputAction.setLabelText("Output");
        outputAction.setLabelVisibility(View.VISIBLE);
        outputAction.setImageResource(R.drawable.export);
        outputAction.setColorNormalResId(R.color.brightBlue);
        outputAction.setColorRipple(getColor(R.color.colorOrange));

        voiceAction = (FloatingActionButton) findViewById(R.id.voice_view_action);
        voiceAction.setButtonSize(FloatingActionButton.SIZE_MINI);
        voiceAction.setLabelText("Voice Input");
        voiceAction.setLabelVisibility(View.VISIBLE);
        voiceAction.setImageResource(R.drawable.microphone);
        voiceAction.setColorNormalResId(R.color.brightBlue);
        voiceAction.setColorRipple(getColor(R.color.colorOrange));


        previewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewMode = VIEW_MODE_RGB;
            }
        });
        hsvAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewMode = VIEW_MODE_HSV;
            }
        });
        contourAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewMode = VIEW_MODE_CONTOURS;
            }
        });
        outputAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewMode = VIEW_MODE_OUTPUT;
            }
        });
        voiceAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerFragment.promptSpeechInput();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                CameraView.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(CameraView.this);
        navigationView.setItemIconTintList(null);

        Menu menu = navigationView.getMenu();
        networkTablesIcon = menu.findItem(R.id.networktables_icon);
        //udpIcon = menu.findItem(R.id.udb_connection);
        trackingStatus = menu.findItem(R.id.tracking_status);

        cameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        cameraView.enableFpsMeter();
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(CameraView.this);
        updateProcessingSettings();

        Networking.initNetworkTables(networktableName); //Vision Table
        Networking.initUDPServer();

        Bundle HSVSavedData = getIntent().getExtras();
        if (HSVSavedData != null) {
            curHueRange = HSVSavedData.getIntArray("hue");
            curSatRange = HSVSavedData.getIntArray("sat");
            curValRange = HSVSavedData.getIntArray("val");
            Log.d("Extrahmin", "" + HSVSavedData.getInt("HueMin"));
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        listenerFragment.stopRecognizer();

        if (cameraView != null && !HSVTunerActivity.isPopUp()) {
            Log.d(TAG, "Camera Disabled");
            cameraView.disableView();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        listenerFragment.shutdown();
        if (cameraView != null) {
            Log.d(TAG, "Camera Destroy");
            cameraView.disableView();
        }
    }

    public void onResume() {
        super.onResume();
        Networking.reconnectNetworkTable();
        listenerFragment.startRecognizer();
        updateProcessingSettings();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV Initialized");
            HSVTunerActivity.setIsRunning(false);
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV did not load");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, CameraView.this, mLoaderCallback);
        }

        Bundle HSVSavedData = getIntent().getExtras();
        if (HSVSavedData != null) {
            curHueRange = HSVSavedData.getIntArray("hue");
            curSatRange = HSVSavedData.getIntArray("sat");
            curValRange = HSVSavedData.getIntArray("val");
            Log.d("Extrahmin", "" + HSVSavedData.getInt("HueMin"));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mSaveOptions = menu.add("Save/Load Options");
        mSettingsViewer = menu.add("View Current Settings");
        getMenuInflater().inflate(R.menu.camera_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item == mSaveOptions) {
            startActivity(new Intent(CameraView.this, SaveSettings.class));

        }
        if (item == mSettingsViewer) {
            startActivity(new Intent(CameraView.this, SettingsViewer.class));

        }
        return super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.reg_camera) {
            mViewMode = VIEW_MODE_RGB;

        } else if (id == R.id.hsv_view) {

            Bundle HSVSavedData = getIntent().getExtras();
            if (HSVSavedData != null) {
                curHueRange = HSVSavedData.getIntArray("hue");
                curSatRange = HSVSavedData.getIntArray("sat");
                curValRange = HSVSavedData.getIntArray("val");
                Log.d("Extrahmin", "" + HSVSavedData.getInt("HueMin"));
            }

            Intent launchNewActivity = new Intent(CameraView.this, HSVTunerActivity.class);

            launchNewActivity.putExtra("Hue Data", curHueRange);
            launchNewActivity.putExtra("Sat Data", curSatRange);
            launchNewActivity.putExtra("Val Data", curValRange);


            startActivityForResult(launchNewActivity, 0);
            HSVTunerActivity.setIsRunning(true);
            mViewMode = VIEW_MODE_HSV;

        } else if (id == R.id.contour_view) {
            mViewMode = VIEW_MODE_CONTOURS;
        } else if (id == R.id.filter_settings) {

            Bundle HSVSavedData = getIntent().getExtras();
            if (HSVSavedData != null) {
                curHueRange = HSVSavedData.getIntArray("hue");
                curSatRange = HSVSavedData.getIntArray("sat");
                curValRange = HSVSavedData.getIntArray("val");
            }

            Intent launchNewActivity = new Intent(CameraView.this, FilterSettings.class);
            startActivity(launchNewActivity);

            Intent NewActivity = new Intent(CameraView.this, HSVTunerActivity.class);

            NewActivity.putExtra("Hue Data", curHueRange);
            NewActivity.putExtra("Sat Data", curSatRange);
            NewActivity.putExtra("Val Data", curValRange);

            HSVTunerActivity.setIsRunning(true);
            mViewMode = VIEW_MODE_CONTOURS;
        } else if (id == R.id.output_view) {
            mViewMode = VIEW_MODE_OUTPUT;
        } else if (id == R.id.app_settings) {
            Intent launchNewActivity = new Intent(CameraView.this, SettingsActivity.class);
            startActivity(launchNewActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onCameraViewStarted(int width, int height) {
        Networking.reconnectNetworkTable();

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mHsv = new Mat(height, width, CvType.CV_8UC1);
        mContour = new Mat(height, width, CvType.CV_8UC1);
        mOutput = new Mat(height, width, CvType.CV_8UC4);
        pipeline = new VisionPipeline(width, height);

        fieldOfView = getFieldOfViewVertical();
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mHsv.release();
        mContour.release();
        mOutput.release();
        pipeline.release();
        Log.d(TAG, "CameraView Stopped");
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        checkConnections();
        updateProcessingSettings();
        mRgba.release();
        mRgba = inputFrame.rgba();
        Networking.sendNumberNT("System Time", System.currentTimeMillis());
        Networking.sendBooleanNT("Phone Connected", Networking.isConnectedNT());

        int orientation_P = Configuration.ORIENTATION_PORTRAIT;
        int orientation_L = Configuration.ORIENTATION_LANDSCAPE;

        if (orientation_L == this.getResources().getConfiguration().orientation && !landscape180) {
           Mat mRgbaN = mRgba;
            Core.flip(mRgba, mRgbaN, -1);
            mRgba = mRgbaN;
        }

        else if (orientation_P == this.getResources().getConfiguration().orientation && !portrait180) {
            Mat mRgbaT = mRgba.t();
            Core.flip(mRgbaT, mRgbaT, 1);
            Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());
            mRgba = mRgbaT;
        }
        else if (orientation_P == this.getResources().getConfiguration().orientation && portrait180) {
            Mat mRgbaT = mRgba.t();
            Core.flip(mRgbaT, mRgbaT, -1);
            Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());
            mRgba = mRgbaT;
        }

        Log.d("FieldOfView", "" + JavaCameraView.fieldOfViewVertical);
        Log.d("FPS", "" + FpsMeter.fps());

        targets = new Targets();

        if (HSVTunerActivity.hasStarted()) {

            curHueRange = HSVTunerActivity.getHueRange();
            curSatRange = HSVTunerActivity.getSatRange();
            curValRange = HSVTunerActivity.getValRange();
        }

        hueMin = curHueRange[0];
        hueMax = curHueRange[1];
        satMin = curSatRange[0];
        satMax = curSatRange[1];
        valMin = curValRange[0];
        valMax = curValRange[1];

        if (mViewMode != VIEW_MODE_RGB)
            pipeline.process(mRgba, curHueRange,
                    curSatRange,
                    curValRange);

        switch (mViewMode) {
            case VIEW_MODE_HSV:
                updateProcessingSettings();
                mHsv = pipeline.hsvThresholdOutput();
                mOutput = mHsv;
                break;
            case VIEW_MODE_RGB:
                updateProcessingSettings();
                mOutput = mRgba;
                break;
            case VIEW_MODE_CONTOURS:
                updateProcessingSettings();
                mOutput = mRgba;
                Imgproc.drawContours(mOutput, pipeline.filterContoursOutput(), -1, new Scalar(255, 153, 0), 2);
                break;
            case VIEW_MODE_OUTPUT:
                updateProcessingSettings();
                mOutput = pipeline.drawBoundRect(mRgba);
                updateNetworkTables();
                trackingStatusBoolean = true;
                Log.d("Horizontal Angle", "" + getAngleHorizontal(Targets.avgCenter()));
                Log.d("Distance", "" + getDistance(getWidth(), getFieldOfViewVertical()/2, Targets.entireWidth()));
                Log.d("Field of View Vertical", "" + getFieldOfViewVertical());
                try {
                    updateUDP();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return mOutput;
    }

    public void updateNetworkTables() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(CameraView.this);

        if (sharedPref.getBoolean(NTSettingsActivity.NT_PREF_CONNECTED, true))
            Networking.sendBooleanNT(NTSettingsActivity.NT_PREF_CONNECTED, Networking.isConnectedNT());
        if (sharedPref.getBoolean(NTSettingsActivity.NT_PREF_TRACKED, true))
            Networking.sendNumberNT(NTSettingsActivity.NT_PREF_TRACKED, Targets.numOfTargets());
        if (sharedPref.getBoolean(NTSettingsActivity.NT_PREF_FPS, true))
            Networking.sendNumberNT(NTSettingsActivity.NT_PREF_FPS, Math.round(FpsMeter.fps() * 100.0) / 100.0);
        if (sharedPref.getBoolean(NTSettingsActivity.NT_PREF_HEIGHT, false))
            Networking.sendNumberNT(NTSettingsActivity.NT_PREF_HEIGHT, getHeight());
        if (sharedPref.getBoolean(NTSettingsActivity.NT_PREF_WIDTH, false))
            Networking.sendNumberNT(NTSettingsActivity.NT_PREF_WIDTH, getWidth());
        if (sharedPref.getBoolean(NTSettingsActivity.NT_PREF_FOV_H, false))
            Networking.sendNumberNT(NTSettingsActivity.NT_PREF_FOV_H, getFieldOfViewHorizontal());
        if (sharedPref.getBoolean(NTSettingsActivity.NT_PREF_FOV_V, false))
            Networking.sendNumberNT(NTSettingsActivity.NT_PREF_FOV_V, getFieldOfViewVertical());
    }

    public void updateUDP() throws JSONException {
        JSONObject json = new JSONObject();
        SharedPreferences sharedPref = getDefaultSharedPreferences(CameraView.this);

        if (sharedPref.getBoolean(UDPSettingsActivity.UDP_PREF_ANGLE_H, true))
            json.put(UDPSettingsActivity.UDP_PREF_ANGLE_H, getAngleHorizontal(Targets.avgCenter()));
        if (sharedPref.getBoolean(UDPSettingsActivity.UDP_PREF_ANGLE_V, false))
            json.put(UDPSettingsActivity.UDP_PREF_ANGLE_V, getAngleVertical(Targets.avgY()));
        if (sharedPref.getBoolean(UDPSettingsActivity.UDP_PREF_COORDINATES, false))
            json.put(UDPSettingsActivity.UDP_PREF_COORDINATES, Targets.getListOfCoordinates());
        if (sharedPref.getBoolean(UDPSettingsActivity.UDP_PREF_SYS_TIME, false))
            json.put(UDPSettingsActivity.UDP_PREF_SYS_TIME, System.currentTimeMillis() / 1000);
        if (sharedPref.getBoolean(UDPSettingsActivity.UDP_PREF_DISTANCE_H, false))
            json.put(UDPSettingsActivity.UDP_PREF_DISTANCE_H, getDistance(getWidth(), getFieldOfViewHorizontal()/2, Targets.entireWidth() ));
        if (sharedPref.getBoolean(UDPSettingsActivity.UDP_PREF_DISTANCE_V, false))
            json.put(UDPSettingsActivity.UDP_PREF_DISTANCE_V, getDistance(getWidth(), getFieldOfViewVertical()/2, Targets.entireWidth()));

        Networking.sendDataUDP(json);
    }

    public void updateProcessingSettings() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(CameraView.this);
        /*Setting FPS**/
        String FPS = sharedPref.getString(ProcessingSettingsActivity.PROCESSING_PREF_FPS, "30000");
        int iFPS = Integer.parseInt(FPS);
        JavaCameraView.manualFPS = iFPS;

        /*Setting Resoulution**/
        int setResolution = Integer.parseInt(sharedPref.getString(ProcessingSettingsActivity.PROCESSING_PREF_RESOLUTION, "5"));
        if (setResolution == 640480) {
            changeResolution(2, 640, 480);
        } else if (setResolution == 320240) {
            changeResolution(3, 320, 240);
        } else if (setResolution == 720480) {
            changeResolution(1, 720, 480);
        } else if (setResolution == 1280720) {
            changeResolution(0, 1280, 720);
        } else {
            changeResolution(2, 640, 480);
        }
        /*Setting IP**/
        String robotIP = sharedPref.getString(ProcessingSettingsActivity.PROCESSING_PREF_IP, "192.168.42.44");
        Log.d("ROBOTIP", robotIP);
        Networking.ROBORIO_ADDRESS = robotIP;

        /*Setting Networktable Name**/
        String networktableName = sharedPref.getString(ProcessingSettingsActivity.PROCESSING_PREF_NETWORKTABLES_NAME, "SmartDashboard");
        this.networktableName = networktableName;

        /*Setting Portrait Orientation**/
        int portraitOrientationCode = Integer.parseInt(sharedPref.getString(ProcessingSettingsActivity.PROCESSING_PREF_PORTRAIT_ORIENTATION, "0"));
        if (portraitOrientationCode == 0) {
            portrait180 = false;
        } else {
            portrait180 = true;
        }
        /*Setting Landscape Orientation**/
        int landscapeOrientationCode = Integer.parseInt(sharedPref.getString(ProcessingSettingsActivity.PROCESSING_PREF_LANDSCAPE_ORIENTATION, "0"));
        if (landscapeOrientationCode == 0) {
            landscape180 = false;
        } else {
            landscape180 = true;
        }
        /*Setting Target Width**/
        int targetWidth = Integer.parseInt(sharedPref.getString(ProcessingSettingsActivity.PROCESSING_PREF_TARGET_WIDTH, "10"));
        this.targetWidth = targetWidth;
        /*Setting Zoom**/
        int zoom = Integer.parseInt(sharedPref.getString(ProcessingSettingsActivity.PROCESSING_PREF_ZOOM, "0"));
        JavaCameraView.zoomVal = zoom;
        /*Setting White Balance**/
        int whiteBalanceCode = Integer.parseInt(sharedPref.getString(ProcessingSettingsActivity.PROCESSING_PREF_WHITE_BALANCE, "0"));
        JavaCameraView.whiteBalanceCode = whiteBalanceCode;
        /*Setting Colour Effect**/
        int colourEffectCode = Integer.parseInt(sharedPref.getString(ProcessingSettingsActivity.PROCESSING_PREF_COLOUR_EFFECT, "0"));
        JavaCameraView.colourEffectCode = colourEffectCode;
    }

    public static void changeResolution(int id, int maxWidth, int maxHeight) {
        currentResolutionID = id;
        cameraView.setMaxFrameSize(maxWidth, maxHeight);
    }

    public static int getCurrentResolutionID() {
        return currentResolutionID;
    }

    public static double getFieldOfViewVertical() {
        return JavaCameraView.fieldOfViewVertical;
    }

    public static double getFieldOfViewHorizontal() {
        return JavaCameraView.fieldOfViewHorizontal;
    }

    public static double getFocalLengthVertical() {
        return getWidth() / (2 * Math.tan((Math.toRadians(getFieldOfViewVertical() / 2))));
    }

    public static double getFocalLengthHorizontal() {
        return getHeight() / (2 * Math.tan((Math.toRadians(getFieldOfViewHorizontal() / 2))));
    }

    public static int getWidth() {
        return JavaCameraView.currentWidth;
    }

    public static int getHeight() {
        return JavaCameraView.currentHeight;
    }

    public double getAngleHorizontal(double center) {
        if (Configuration.ORIENTATION_LANDSCAPE == this.getResources().getConfiguration().orientation)
            return Math.toDegrees(Math.atan((center - (getWidth() / 2)) / getFocalLengthVertical()));
        else
            return Math.toDegrees(Math.atan((center - (getWidth() / 2)) / getFocalLengthHorizontal()));
    }

    public double getAngleVertical(double center) {
        if (Configuration.ORIENTATION_LANDSCAPE == this.getResources().getConfiguration().orientation)
            return Math.toDegrees(Math.atan((center - (getHeight() / 2)) / getFocalLengthVertical()));
        else
            return Math.toDegrees(Math.atan((center - (getHeight() / 2)) / getFocalLengthHorizontal()));
    }

    private void checkConnections() {
        if (Networking.isConnectedNT() && !connectedNT) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    networkTablesIcon.setIcon(R.mipmap.ic_launcher);
                }
            });
            connectedNT = true;
        } else if (!Networking.isConnectedNT() && connectedNT) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    networkTablesIcon.setIcon(R.mipmap.ic_launcher);
                }
            });
            connectedNT = false;
        }

        /*if (Networking.isConnectedUDP() && !connectedUDP) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    udpIcon.setIcon(R.mipmap.ic_check_mark);
                }
            });
            connectedUDP = true;
        } else if (!Networking.isConnectedUDP() && connectedUDP) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    udpIcon.setIcon(R.mipmap.ic_red_x);
                }
            });
            connectedUDP = false;
        }*/
    }

    public double getDistance(int fieldOfViewPixels, double halfLensFOV,  double targetWidthPixels) {
        //visionDistance = targetWidthInches*fovPixels/(2*targetWidthPixels*Math.tan(lensFOV));
        double visionDistance = targetWidth*fieldOfViewPixels/(2*targetWidthPixels*Math.tan(halfLensFOV));
        //double gamma = 90 - getAngleVertical(Targets.avgY());
        //return 5.5 / Math.cos(Math.toRadians(gamma));

        return visionDistance;
    }

    public void setDefaultSettings(){

        SharedPreferences loadDefaultSettingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (loadDefaultSettingsPrefs.getInt("savedBoundRect", 0) != 0) {

            //Loading # of Bounding Rectangles
            int boundR = loadDefaultSettingsPrefs.getInt("savedBoundRect", VisionPipeline.BOUND_RECTS);
            VisionPipeline.boundRects = boundR;

            //Loading Resolution Settings
            int ID = loadDefaultSettingsPrefs.getInt("savedResolutionID", 0);
            int mWidth = loadDefaultSettingsPrefs.getInt("savedResolutionWidth", 640);
            int mHeight = loadDefaultSettingsPrefs.getInt("savedResolutionHeight", 480);
            //CameraView.changeResolution(ID, mWidth, mHeight);

            //Loading H,S,V Settings
            int HMin = loadDefaultSettingsPrefs.getInt("savedHMin", 0);
            int HMax = loadDefaultSettingsPrefs.getInt("savedHMax", 180);
            Log.d("HueMinSaved", "" + HMin);

            int SMin = loadDefaultSettingsPrefs.getInt("savedSMin", 0);
            int SMax = loadDefaultSettingsPrefs.getInt("savedSMax", 255);

            int VMin = loadDefaultSettingsPrefs.getInt("savedVMin", 0);
            int VMax = loadDefaultSettingsPrefs.getInt("savedVMax", 255);

            //Sending HSV data to CameraView class to be used
            int[] Hue = new int[]{HMin, HMax};
            int[] Sat = new int[]{SMin, SMax};
            int[] Val = new int[]{VMin, VMax};

            this.curHueRange = Hue;
            this.curSatRange = Sat;
            this.curValRange = Val;

            //Loading Filter Settings
            /*//*Load Min/Max Area**//**/
            int minArea = loadDefaultSettingsPrefs.getInt("savedMinArea", (int) VisionPipeline.MIN_AREA);
            VisionPipeline.filterContoursMinArea[0] = minArea;
            Log.d("loadedSetting", "" + minArea);
            int maxArea = loadDefaultSettingsPrefs.getInt("savedMaxArea", 5000);
            VisionPipeline.filterContoursMinArea[1] = maxArea;

            /*//*Load Min/Max Perimeter**//**/
            int minPerimeter = loadDefaultSettingsPrefs.getInt("savedMinPerimeter", (int) VisionPipeline.MIN_PERIMETER);
            VisionPipeline.filterContoursMinPerimeter[0] = minPerimeter;

            int maxPerimeter = loadDefaultSettingsPrefs.getInt("savedMaxPerimeter", 1000);
            VisionPipeline.filterContoursMinPerimeter[1] = maxPerimeter;

            /*//*Load Min/Max Width**//**/
            int minWidth = loadDefaultSettingsPrefs.getInt("savedMinWidth", 0);
            VisionPipeline.filterContoursWidth[0] = minWidth;

            int maxWidth = loadDefaultSettingsPrefs.getInt("savedMaxWidth", 1000);
            VisionPipeline.filterContoursWidth[1] = maxWidth;

            /*//*Load Min/Max Height**//**/
            int minHeight = loadDefaultSettingsPrefs.getInt("savedMinHeight", 0);
            VisionPipeline.filterContoursHeight[0] = minHeight;

            int maxHeight = loadDefaultSettingsPrefs.getInt("savedMaxHeight", 1000);
            VisionPipeline.filterContoursHeight[1] = maxHeight;

            /*//*Load Min/Max Solidity**//**/
            int minSolidity = loadDefaultSettingsPrefs.getInt("savedMinSolidity", 0);
            VisionPipeline.filterContoursSolidity[0] = minSolidity;

            int maxSolidity = loadDefaultSettingsPrefs.getInt("savedMaxSolidity", 100);
            VisionPipeline.filterContoursSolidity[1] = maxSolidity;

            /*//*Load Min/Max Vertices**//**/
            int minVertices = loadDefaultSettingsPrefs.getInt("savedMinVertices", 0);
            VisionPipeline.filterContoursVertices[0] = minVertices;

            int maxVertices = loadDefaultSettingsPrefs.getInt("savedMaxVertices", 1000000);
            VisionPipeline.filterContoursVertices[1] = maxVertices;

            /*//*Load Min/Max Ratio**//**/
            int minRatio = loadDefaultSettingsPrefs.getInt("savedMinRatio", 0);
            VisionPipeline.filterContoursRatio[0] = minRatio;

            int maxRatio = loadDefaultSettingsPrefs.getInt("savedMaxRatio", 1000);
            VisionPipeline.filterContoursRatio[1] = maxRatio;

            Toast.makeText(this,
                    "Defaults Set Successfully",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

package theoryvision.theory6.controls.com.theoryvision;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

/**
 * Class that handles the loading screen time, loading gif and app permissions
 * @author RickHansenRobotics
 * @since 2017-08-16
 */

public class WelcomeScreen extends AppCompatActivity{

    private static final int WELCOME_TIMEOUT = 4000;
    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int PERMISSIONS_REQUEST = 1;

    GifImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_view);

        int permissionCheck = ContextCompat.checkSelfPermission(WelcomeScreen.this, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WelcomeScreen.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST);
        }else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (!sharedPref.getBoolean(SettingsActivity.KEY_WELCOME, true)) {
                Intent intent = new Intent(WelcomeScreen.this, CameraView.class);
                startActivity(intent);
                finish();
            } else {
                image = (GifImageView) findViewById(R.id.gifView);
                Random r = new Random();

                switch (r.nextInt(7)) {
                    case 0:
                        image.setImageResource(R.drawable.circlewave);
                        break;
                    case 1:
                        image.setImageResource(R.drawable.cube);
                        break;
                    case 2:
                        image.setImageResource(R.drawable.cuberoll);
                        break;
                    case 3:
                        image.setImageResource(R.drawable.roll);
                        break;
                    case 4:
                        image.setImageResource(R.drawable.sphere);
                        break;
                    case 5:
                        image.setImageResource(R.drawable.spherewave);
                        break;
                    case 6:
                        image.setImageResource(R.drawable.spiral);
                        break;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(WelcomeScreen.this, CameraView.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        finish();
                    }
                }, WELCOME_TIMEOUT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(WelcomeScreen.this, CameraView.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "All permissions have not been granted", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}

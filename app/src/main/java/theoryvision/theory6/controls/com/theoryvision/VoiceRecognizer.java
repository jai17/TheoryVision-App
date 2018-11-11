package theoryvision.theory6.controls.com.theoryvision;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

/**
 * Class that handles the Shadow voice recognizer and Shadow usage options
 * @author RickHansenRobotics
 * @since 2017-08-16
 */

public class VoiceRecognizer extends Fragment implements RecognitionListener, TextToSpeech.OnInitListener {
    private boolean hasSpoken = false;
    private boolean hasStarted = false;
    private boolean setupComplete = false;

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "ok shadow";
    private static final String KWS_SEARCH = "wakeup";

    private SpeechRecognizer recognizer;
    private TextToSpeech myTTS;

    private Context context;
    private ShadowOptions options;

    public VoiceRecognizer(){

    }

    @Override
    public void onAttach(Context mContext){
        super.onAttach(mContext);

        context = mContext;
        Log.d("TEST", "attach");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if(sharedPref.getBoolean(SettingsActivity.KEY_PREF_ASSISTANT, false))
            runRecognizerSetup();

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, 102);

        options = new ShadowOptions();

        Log.d("TEST", "on create");
    }

    public void promptSpeechInput(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2500);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Command");

        try {
            startActivityForResult(intent, 101);
        }
        catch (ActivityNotFoundException e){
            Log.d("TEST", "Not supported");
        }
    }

    public void onActivityResult(int request_code, int result_code, Intent i){
        super.onActivityResult(request_code, result_code, i);

        switch(request_code){
            case 101:
                if(result_code == Activity.RESULT_OK && i != null){
                    ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("TEST", result.get(0));
                    String response = options.getResponse(result.get(0));
                    showResponse(response);
                }
                hasSpoken = false;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startRecognizer();
                break;
            case 102:
                if (result_code == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    myTTS = new TextToSpeech(context, this);
                }
                else {
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }
        }
    }

    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(context);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Log.d("TEST", "Failed to init recognizer");
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Listener Started", Snackbar.LENGTH_LONG).show();
                    switchSearch(KWS_SEARCH);
                    hasStarted = true;
                    setupComplete = true;
                }
            }
        }.execute();
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE)) {
            if(!hasSpoken){
                hasSpoken = true;
                Log.d("TEST", "Hi 1241!");
                myTTS.speak("Hi 1241", TextToSpeech.QUEUE_FLUSH, null);
            }
            if(!myTTS.isSpeaking()) {
                recognizer.stop();
                hasStarted = false;
                promptSpeechInput();
            }
        }
        else
            Log.d("TEST", text);
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {

        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        stopRecognizer();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            startRecognizer();
        else
            recognizer.startListening(searchName, 10000);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                //.setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
    }

    @Override
    public void onError(Exception error) {
        Log.d("TEST", "ERROR: " + error.getMessage());
    }

    @Override
    public void onTimeout() {
        Log.d("TEST", "Timed out");
    }

    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(Locale.getDefault());
        }
    }

    public void shutdown(){
        if(myTTS != null) {
            myTTS.stop();
            myTTS.shutdown();
        }

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    public void stopRecognizer(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(hasStarted && !sharedPref.getBoolean(SettingsActivity.KEY_PREF_ASSISTANT, false)) {
            hasStarted = false;
            Log.d("TEST", "stopped");
            recognizer.stop();
        }
    }

    public boolean hasStarted(){
        Log.d("TEST", ""+hasStarted);
        return hasStarted;
    }

    public void startRecognizer(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!setupComplete && sharedPref.getBoolean(SettingsActivity.KEY_PREF_ASSISTANT, false)) {
            runRecognizerSetup();
            setupComplete = true;
        }

        if(!hasStarted && recognizer != null && sharedPref.getBoolean(SettingsActivity.KEY_PREF_ASSISTANT, false)){
            recognizer.startListening(KWS_SEARCH);

            Log.d("TEST", "started");
            hasStarted = true;
        }
    }

    public void showResponse(String response){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context).setTitle("Shadow's Response").setMessage(response);
        dialog.setIcon(R.mipmap.ic_launcher_round);
        dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();

        myTTS.speak(response, TextToSpeech.QUEUE_FLUSH, null);

        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 5000);
    }
}

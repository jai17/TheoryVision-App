package theoryvision.theory6.controls.com.theoryvision;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Class that handles the commands the Shadow voice recognizer can execute and the corresponding responses
 * @author RickHansenRobotics
 * @since 2017-08-16
 */

public class ShadowOptions {

    ArrayList<String> commands;

    public ShadowOptions(){
        commands = new ArrayList<>();

        commands.add("what is the battery voltage");
        commands.add("how many targets do you see");
        commands.add("is network tables connected");
        commands.add("what is the selected auto");
        commands.add("what is the pressure");
    }

    public String getResponse(String question){
        String response = "";
        int commandNumber = commands.size();

        for(int i = 0; i < commands.size(); i++){
            Log.d("TEST", "Similarity " + i + ": " + getSimilarity(commands.get(i), question));
            if(getSimilarity(commands.get(i), question) < 10)
                commandNumber = i;
        }

        switch (commandNumber){
            case 0:
                response = "it is " + Networking.getNumberNT("Battery Voltage") + " volts";
                break;
            case 1:
                if(Targets.numOfTargets() == 1)
                    response = "tracking 1 target";
                else
                    response = "tracking " + Targets.numOfTargets() + " targets";
                break;
            case 2:
                if(Networking.isConnectedNT())
                    response = "network tables is connected";
                else
                    response = "network tables is not connected";
                break;
            case 3:
                response = "autos not available";
                break;
            case 4:
                response = "pressure not available";
                break;
            default:
                response = "I do not know";
                break;
        }

        return response;
    }

    public int getSimilarity(String command, String question){
        return StringUtils.getLevenshteinDistance(command, question);
    }
}

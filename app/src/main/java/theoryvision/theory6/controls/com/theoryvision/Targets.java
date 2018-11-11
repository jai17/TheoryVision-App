package theoryvision.theory6.controls.com.theoryvision;

import android.util.Log;

import java.util.ArrayList;

/**
 *
 * Class in which TargetData from each individual bounding rectangle can be accessed and used to calculate necessary outpus
 * Inputs taken from TargetData
 *
 * @author RickHansenRobotics
 * @since 2017-07-28
 *
 */

public class Targets {

    private static ArrayList<TargetData> targetData;
    private static ArrayList<Double> targetArea;
    public static boolean targets = false;

    Targets() {

        }

    public static double avgCenter(){
        targetData = VisionPipeline.targetData;
        double avgX = 0;
        for (int i = 0; i < targetData.size(); i++) {
        avgX += targetData.get(i).avgX();
        }
        Log.d("AverageX", "" + avgX);
        return avgX /(targetData.size());
        }   //Working

    public static double avgY(){
        targetData = VisionPipeline.targetData;
        double avgY = 0;
        for (int i = 0; i < targetData.size(); i++) {
            avgY += targetData.get(i).avgY();
        }
        return avgY /(targetData.size());
    }          //Working

    public static double avgArea(){
        targetData = VisionPipeline.targetData;
        double avgArea = 0;
        for (int i = 0; i < targetData.size(); i++) {
            avgArea += targetData.get(i).area();
        }
        return avgArea /(targetData.size());
    }               //Working

    public static double areaRatio(){
        targetArea = VisionPipeline.targetArea;
        if(targetData.size() > 1) {
            if(targetData.get(targetData.size() - 1).avgX() < targetData.get(0).avgX())
                return targetArea.get(targetData.size() - 1) / targetArea.get(0);
            else
                return targetArea.get(0) / targetArea.get(targetData.size() - 1);
        }
        else if (targetData.size() == 1)
            return 0;
        else
            return -1;
    }

    public static double entireWidth(){
        targetData = VisionPipeline.targetData;
        if(targetData.size() == 2) {
            if(targetData.get(targetData.size() - 1).avgX() < targetData.get(0).avgX())
                return targetData.get(targetData.size() - 1).x1 - targetData.get(0).x4;
            else
                return targetData.get(targetData.size() - 1).x4 - targetData.get(0).x1;
        }
        else if (targetData.size() == 1)
            return targetData.get(0).width();
        else
            return 1;
    }

    public static TargetData getTargetData(int pos){
        targetData = VisionPipeline.targetData;
        if(targetData.isEmpty())
            return new TargetData();
        else if(pos <= targetData.size()-1)
            return targetData.get(pos);
        else
            return new TargetData();
    }

    public static String getListOfCoordinates(){
        String coordinates = "";
        targetData = VisionPipeline.targetData;

        if(targetData.size() > 10)
            return "Too many targets";
        else if(targetData.size() > 0){
            for (int i = 0; i < targetData.size(); i++) {
                coordinates += targetData.get(i).coordinates();
                coordinates += ";";
            }
            return coordinates;
        }
        else
            return "0";
    }

    public static int numOfTargets(){
        targetData = VisionPipeline.targetData;
        if(targetData.isEmpty())
            return 0;
        else
            targets = true;
            return targetData.size();
    }

}
package theoryvision.theory6.controls.com.theoryvision;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * Main Pipeline in which HSV Threshold, Contours, and Bounding Rectangles are calculated
 * Contours can be filtered through Min Area etc.
 * Inputs taken from UI or ssaved settings
 *
 * @author RickHansenRobotics
 * @since 2017-06-21
 *
 */
public class VisionPipeline {

    public static final double MIN_AREA = 30.0;
    public static final double MIN_PERIMETER = 0.0;
    public static final int BOUND_RECTS = 2;
    public static final double EMPTY = 0.0;

    private int width;
    private int height;

    static double focalLength;

    public static double[][] curSettings;
    public static double[] filterContoursMinArea = {MIN_AREA, 20000};
    public static double[] filterContoursMinPerimeter = {MIN_PERIMETER, 1000};
    public static double[] filterContoursWidth = {0, 1000};
    public static double[] filterContoursHeight = {0, 1000};
    public static double[] filterContoursSolidity = {0, 100};
    public static double[] filterContoursVertices = {0, 1000000};
    public static double[] filterContoursRatio = {0, 1000};
    public static double boundRects = 2.0;
    public static double empty = 0.0;


    private Mat hsvThresholdOutput;

    public double [] point1 = new double[2];
    public double [] point2 = new double[2];
    public double [] point3 = new double[2];
    public double [] point4 = new double[2];

    public static ArrayList<TargetData> targetData = new ArrayList<>();
    public static ArrayList<Double> targetArea = new ArrayList<>();


    public VisionPipeline(int width, int height){
        this.width = width;
        this.height = height;

        hsvThresholdOutput = new Mat(width, height, CvType.CV_8UC1);

        curSettings = new double[][]{filterContoursMinArea, filterContoursMinPerimeter,
                                     filterContoursWidth, filterContoursHeight,
                                     filterContoursSolidity, filterContoursVertices,
                                     filterContoursRatio, {boundRects, empty}};
    }
    //Outputs
    private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
    private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();


    /**
     * This is the primary method that runs the entire pipeline and updates the outputs.
     */
    public void process(Mat source0, int[] hsvThresholdHue, int[] hsvThresholdSaturation, int[] hsvThresholdValue) {
        // Step HSV_Threshold0
        hsvThreshold(source0, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, hsvThresholdOutput);

        // Step Find_Contours0:
        Mat findContoursInput = hsvThresholdOutput;
        boolean findContoursExternalOnly = true;
        findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);

        // Step Filter_Contours0:
        ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;

        filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursWidth, filterContoursHeight, filterContoursSolidity, filterContoursVertices, filterContoursRatio, filterContoursOutput);

    }

    public static void changeSettings(double[][] settings){
        filterContoursMinArea = settings[0];
        filterContoursMinPerimeter = settings[1];
        filterContoursWidth = settings[2];
        filterContoursHeight = settings[3];
        filterContoursSolidity = settings[4];
        filterContoursVertices = settings[5];
        filterContoursRatio = settings[6];
        boundRects = settings[7][0];
        empty = settings[7][1];

        curSettings = settings;
    }

    public static double[][] currentSettings(){
        return curSettings;
    }

    /**
     * This method is a generated getter for the output of a HSV_Threshold.
     * @return Mat output from HSV_Threshold.
     */
    public Mat hsvThresholdOutput() {
        return hsvThresholdOutput;
    }

    /**
     * This method is a generated getter for the output of a Find_Contours.
     * @return ArrayList<MatOfPoint> output from Find_Contours.
     */
    public ArrayList<MatOfPoint> findContoursOutput() {
        return findContoursOutput;
    }

    /**
     * This method is a generated getter for the output of a Filter_Contours.
     * @return ArrayList<MatOfPoint> output from Filter_Contours.
     */
    public ArrayList<MatOfPoint> filterContoursOutput() {
        Collections.sort(filterContoursOutput, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint matOfPoint, MatOfPoint t1) {
                return -Double.compare(Imgproc.contourArea(matOfPoint), Imgproc.contourArea(t1));
            }
        });
        return filterContoursOutput;
    }

    public void release(){
        hsvThresholdOutput.release();
    }


    /**
     * Segment an image based on hue, saturation, and value ranges.
     *
     * @param input The image on which to perform the HSL threshold.
     * @param hue The min and max hue
     * @param sat The min and max saturation
     * @param val The min and max value
     * @param out The image in which to store the output.
     */
    private void hsvThreshold(Mat input, int[] hue, int[] sat, int[] val,
                              Mat out) {
        Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
        Core.inRange(out, new Scalar(hue[0], sat[0], val[0]),
                new Scalar(hue[1], sat[1], val[1]), out);
    }

    /**
     * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
     * @param input The image on which to perform the Distance Transform.
     * @param externalOnly The Transform.
     * @param contours the size of the mask.
     */
    private void findContours(Mat input, boolean externalOnly,
                              List<MatOfPoint> contours) {
        Mat hierarchy = new Mat();
        contours.clear();
        int mode;
        if (externalOnly) {
            mode = Imgproc.RETR_EXTERNAL;
        }
        else {
            mode = Imgproc.RETR_LIST;
        }
        int method = Imgproc.CHAIN_APPROX_SIMPLE;
        Imgproc.findContours(input, contours, hierarchy, mode, method);
    }


    /**
     * Filters out contours that do not meet certain criteria.
     * @param inputContours is the input list of contours
     * @param output is the the output list of contours
     * @param minArea is the minimum area of a contour that will be kept
     * @param minPerimeter is the minimum perimeter of a contour that will be kept
     * @param rangeWidth minimum and maximum width of a contour
     * @param rangeHeight minimum and maximum height
     * @param solidity the minimum and maximum solidity of a contour
     * @param rangeVertexCount minimum and maximum vertex Count
     * @param rangeRatio minimum and maximum ratio of width to height
     */
    private void filterContours(List<MatOfPoint> inputContours, double[] minArea,
                                double[] minPerimeter, double[] rangeWidth, double[] rangeHeight,
                                double[] solidity, double[] rangeVertexCount, double[]
                                        rangeRatio, List<MatOfPoint> output) {
        final MatOfInt hull = new MatOfInt();
        output.clear();
        //operation
        for (int i = 0; i < inputContours.size(); i++) {
            final MatOfPoint contour = inputContours.get(i);
            final Rect bb = Imgproc.boundingRect(contour);
            if (bb.width < rangeWidth[0] || bb.width > rangeWidth[1]) continue;
            if (bb.height < rangeHeight[0] || bb.height > rangeHeight[1]) continue;
            final double area = Imgproc.contourArea(contour);
            if (area < minArea[0]) continue;
            if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter[0]) continue;
            Imgproc.convexHull(contour, hull);
            MatOfPoint mopHull = new MatOfPoint();
            mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
            for (int j = 0; j < hull.size().height; j++) {
                int index = (int)hull.get(j, 0)[0];
                double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
                mopHull.put(j, 0, point);
            }
            final double solid = 100 * area / Imgproc.contourArea(mopHull);
            if (solid < solidity[0] || solid > solidity[1]) continue;
            if (contour.rows() < rangeVertexCount[0] || contour.rows() > rangeVertexCount[1])	continue;
            final double ratio = bb.width / (double)bb.height;
            if (ratio < rangeRatio[0] || ratio > rangeRatio[1]) continue;
            output.add(contour);
        }
    }

    public Mat drawBoundRect(Mat mRgba){

        Scalar Blue;
        Scalar Red;
        Scalar Green;
        Mat mOutput = mRgba;
        targetData.clear();
        targetArea.clear();

        for(int i = 0; i < filterContoursOutput().size() && i < boundRects; i++){
            Rect r = Imgproc.boundingRect(filterContoursOutput().get(i));

            Imgproc.rectangle(mOutput, new Point(r.x, r.y), new Point(r.x + r.width, r.y + r.height), new Scalar(255, 0, 0), 5);


            point1[0]= r.x;
            point1[1] = r.y;
            point2[0] = r.x + r.width;
            point2[1] = r.y;
            point3[0] = r.x;
            point3[1] = r.y +r.height;
            point4[0] = r.x + r.width;
            point4[1] = r.y +r.height;

            targetData.add(new TargetData (point1, point2, point3, point4));

            targetArea.add(Imgproc.contourArea(filterContoursOutput().get(i)));
        }
        return mOutput;
    }
}

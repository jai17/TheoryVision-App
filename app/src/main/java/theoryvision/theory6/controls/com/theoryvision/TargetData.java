package theoryvision.theory6.controls.com.theoryvision;

import java.text.MessageFormat;

/**
 *
 * Class in which coordinate data is retrieved for each bounding rectangle and outputs individual outputs (AvgX etc.)
 * Inputs taken from VisionPipeline
 *
 * @author RickHansenRobotics
 * @since 2017-07-28
 *
 */

public class TargetData {

    double x1 = 0;
    double x2 = 0;
    double x3 = 0;
    double x4 = 0;

    double y1 = 0;
    double y2 = 0;
    double y3 = 0;
    double y4 = 0;

    public TargetData(){};

    public TargetData(double [] point1, double [] point2, double [] point3, double [] point4) {

        x1 = point1[0];
        x2 = point2[0];
        x3 = point3[0];
        x4 = point4[0];

        y1 = point1[1];
        y2 = point2[1];
        y3 = point3[1];
        y4 = point4[1];
    }

    public double avgX(){
        return (x1 + x2 + x3 + x4)/4;
    }

    public double avgY(){
        return (y1 + y2 + y3 + y4)/4;
    }

    public double area(){
        return width()*height();
    }

    public double width(){
        return (x4 - x1);
    }

    public double height(){
        return (y4 - y1);
    }

    public String coordinates(){
        return MessageFormat.format("({0},{1}), ({2},{3}), ({4},{5}), ({6},{7})", x1, y1, x2, y2, x3, y3, x4, y4);
    }

}
/* This class can be used to draw a line between two points */

package org.firstinspires.ftc.teamcode.math;

public class Line{
    //variables
    private Point startPoint;
    private Point endPoint;

    //methods
    //constructor - inputs two points and makes line between them
    public Line(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

}
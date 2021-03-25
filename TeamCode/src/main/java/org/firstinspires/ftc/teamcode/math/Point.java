/*This class is used as a way to make points*/

package org.firstinspires.ftc.teamcode.math;
import java.math.*;


public class Point implements Cloneable{
    //variables
    //actual values
    private double x;
    private double y;

    //methods
    //constructor - makes from input x and y
    public Point(double x, double y) {
        //assign values
        this.x = x;
        this.y = y;
    }

    //default constructor at (0,0)
    public Point() {
        this(0,0);
    }

    //clone method
    @Override
    public Point clone() {
        //not worth using super, just copy the fields
        return new Point(this.getX(), this.getY());
    }

    //return angle from the positive x (arctan)
    public double angle() {
        return Math.atan2(y, x);
    }

    //return distance from origin
    public double distance() {
        return Math.sqrt(x*x + y*y);
    }

    //add two points
    public Point plus(Point otherPoint) {
        //add each of the coordinates
        return new Point(this.getX() + otherPoint.getX(),
                this.getY() + otherPoint.getY());
    }

    //subtract two points
    public Point minus(Point otherPoint) {
        return new Point(this.getX() - otherPoint.getX(),
                this.getY() - otherPoint.getY());
    }

    //check for equality with another point
    public boolean equals(Point otherPoint) {
        //check each value individually
        if (MathUtilities.closeEnough(this.getX(), otherPoint.getX())
                && MathUtilities.closeEnough(this.getY(), otherPoint.getY())) {
            return true;
        }
        //otherwise, false
        else {
            return false;
        }
    }

    //scalar multiplication
    public void scale(double scaleFactor) {
        this.setX(scaleFactor * this.getX());
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

}
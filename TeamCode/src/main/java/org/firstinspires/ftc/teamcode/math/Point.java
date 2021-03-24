/*This class is used as a way to make points
* and vectors centered at the origin. If you want a vector,
* you should be able to do almost everything you'd need
* with that using this point class*/

package org.firstinspires.ftc.teamcode.math;



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

    //draw a line from the origin to this point
    public Line lineFromOrigin() {
        return new Line(new Point(), this);
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

    //add two points --- also works to translate them by some amount
    public Point plus(Point otherPoint) {
        //add each of the coordinates
        return new Point(this.getX() + otherPoint.getX(),
                this.getY() + otherPoint.getY());
    }

    //get the same point, but with negative values instead
    public Point negative() {
        return new Point(-1 * this.getX(), -1 * this.getY());
    }

    //subtract two points
    public Point minus(Point otherPoint) {
        return this.plus(this.negative());
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
    public Point scale(double scaleFactor) {
        return new Point(scaleFactor * this.getX(), scaleFactor * this.getY());
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
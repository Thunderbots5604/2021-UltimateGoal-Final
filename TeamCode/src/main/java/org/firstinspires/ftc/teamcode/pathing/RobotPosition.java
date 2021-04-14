package org.firstinspires.ftc.teamcode.pathing;

import org.firstinspires.ftc.teamcode.math.MathUtilities;
import org.firstinspires.ftc.teamcode.math.Point;

public class RobotPosition implements Cloneable {
    //variables
    private Point location;
    private double angle;

    //methods
    //constructor
    public RobotPosition(Point location, double angle) {
        //just assign the values
        this.location = location;
        this.angle = angle % 360;
    }

    //default just does a zero version
    public RobotPosition() {
        this(Point.origin(), 0);
    }

    //zero version as a static method
    public static RobotPosition zero() {
        return new RobotPosition();
    }

    //clone method
    public RobotPosition clone() {
        return new RobotPosition(getLocation(), getAngle());
    }

    //equality
    public boolean equals(RobotPosition otherRobotPosition) {
        if(MathUtilities.closeEnough(getAngle(), otherRobotPosition.getAngle()) && getLocation().equals(otherRobotPosition.getLocation())) {
            return true;
        }
        else {
            return false;
        }
    }

    //within a range of values
    public boolean equalsRange(RobotPosition otherRobotPosition, double linearRange, double angleRange) {
        if(MathUtilities.within(angleRange, getAngle(), otherRobotPosition.getAngle()) && getLocation().equalsRange(otherRobotPosition.getLocation(), linearRange)) {
            return true;
        }
        else {
            return false;
        }
    }

    //addition and subtraction
    public RobotPosition plus(RobotPosition otherRobotPosition) {
        //make a new position from the two others
        return new RobotPosition(getLocation().plus(otherRobotPosition.getLocation()), MathUtilities.angleDifference(getAngle(), -1 * otherRobotPosition.getAngle()));
    }

    public RobotPosition minus(RobotPosition otherRobotPosition) {
        //same as plus, but uses minus instead
        return new RobotPosition(getLocation().minus(otherRobotPosition.getLocation()), MathUtilities.angleDifference(getAngle(), otherRobotPosition.getAngle()));
    }

    //getters
    public Point getLocation(){
        return location.clone();
    }

    public double getAngle(){
        return angle;
    }
}

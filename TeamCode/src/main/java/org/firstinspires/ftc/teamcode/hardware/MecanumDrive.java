/*This class should handle most of the direct setting of powers,
dealing with motors, hardware, etc. that is needed for movement

The autonomous portions are handled by robot since they use a lot of different sensors and stuff
 */

package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.math.Line;
import org.firstinspires.ftc.teamcode.math.MathUtilities;
import org.firstinspires.ftc.teamcode.math.Point;

public class MecanumDrive {
    //fields
    //most important field - the hardware map
    private HardwareMap map;
    //wheels - assumes DcMotorEx
    private DcMotorEx frontLeftMotor;
    private DcMotorEx frontRightMotor;
    private DcMotorEx backLeftMotor;
    private DcMotorEx backRightMotor;
    //motor powers - not instance variables because multiple operations might be done at once
    private double frontLeftMotorPower;
    private double frontRightMotorPower;
    private double backLeftMotorPower;
    private double backRightMotorPower;
    //toggles - reverse and half speed
    private boolean reverse;
    private boolean halfSpeed;
    //multiplier used with the toggles
    private double multiplier;
    //final values should be changed based on testing for the robot each year
    //by default, have these at 1 and then measure the proportions for each wheel
    private static final double FRONT_LEFT_TICK_MULTIPLIER = 1;
    private static final double FRONT_RIGHT_TICK_MULTIPLIER = 1;
    private static final double BACK_LEFT_TICK_MULTIPLIER = 1;
    private static final double BACK_RIGHT_TICK_MULTIPLIER = 1;
    //previous motor encoder values
    private int previousEncoderFL;
    private int previousEncoderFR;
    private int previousEncoderBL;
    private int previousEncoderBR;


    //build a robot based on a hardware map and names of the motors
    public MecanumDrive(HardwareMap map, String frontLeftDrive, String frontRightDrive,
                        String backLeftDrive, String backRightDrive) {
        this.map = map;
        //build the drive system
        frontLeftMotor = map.get(DcMotorEx.class, frontLeftDrive);
        frontRightMotor = map.get(DcMotorEx.class, frontRightDrive);
        backLeftMotor = map.get(DcMotorEx.class, backLeftDrive);
        backRightMotor = map.get(DcMotorEx.class, backRightDrive);
        //automatically set boolean toggles to false
        reverse = false;
        halfSpeed = false;
        //multiplier starts at 1
        multiplier = 1;
        //motor set up stuff
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    //include some default strings for lovable dummies that aren't gonna want to type
    //all of the names in manually
    public MecanumDrive(HardwareMap map) {
        this(map, "lmf", "lmb", "rmf", "rmb");
    }

    //toggles - should be checked *before* motion components to ensure accurately scaled values
    //reverse
    public void toggleReverse() {
        //set the boolean equal to its inverse
        reverse = !reverse;
    }

    //half speed
    public void toggleHalfSpeed() {
        //invert boolean value
        halfSpeed = !halfSpeed;
    }

    //calculate multiplier - similar to updateMotorPowers but for the multiplier from toggles
    //calculates together after a reset to ensure glitches don't occur
    public void calculateMultiplier() {
        //reset multiplier
        resetMultiplier();
        //check for reverse and halfSpeed
        if (reverse) {
            multiplier *= -1;
        }
        if (halfSpeed) {
            multiplier *= 0.5;
        }
    }
    //a multiplier reset, probably doesn't need to be public but is public just in case
    public void resetMultiplier() {
        multiplier = 1;
    }

    //linear motion uses a point as a vector to move
    public void linearMove(Point direction, double power) {
        //scale the direction vector to be no more magnitude than power
        Point scaledDirection = direction.getUnitVector();
        //find the maximum value of sin+cos for the unit vector since it may be greater than 1
        double maxValue = Math.abs(scaledDirection.getX()) + Math.abs(scaledDirection.getY());
        //finish scaling the vector
        scaledDirection.scale(power);
        //set the powers - y - x for front left and back right, y + x for front right and back left
        frontLeftMotorPower += (scaledDirection.getY() - scaledDirection.getX() / maxValue);
        backRightMotorPower += (scaledDirection.getY() - scaledDirection.getX() / maxValue);
        frontRightMotorPower += (scaledDirection.getY() + scaledDirection.getX() / maxValue);
        backLeftMotorPower += (scaledDirection.getY() + scaledDirection.getX() / maxValue);
    }

    //radial motion used for turning only takes power as a variable since it's designed for point turns
    //left is negative, right is positive
    public void radialMove(double power) {
        //positive values go on left side of motors
        frontLeftMotorPower += -1 * power;
        backLeftMotorPower += -1 * power;
        //negative values go on right side of motors
        frontRightMotorPower += power;
        backRightMotorPower += power;
    }

    //reset the motor values for recalculation - DOES NOT STOP MOTORS
    public void resetPowerValues() {
        frontLeftMotorPower = 0;
        frontRightMotorPower = 0;
        backLeftMotorPower = 0;
        backRightMotorPower = 0;
    }

    //update motor powers to the motors
    public void updateMotorPowers() {
        //scale factor for use later
        double maxValue = 1;
        //scale the powers to the maximum if one of them is greater than one
        if (frontLeftMotorPower > 1 || frontRightMotorPower > 1 ||
            backLeftMotorPower > 1 || backRightMotorPower > 1) {
            //take the max of all of their absolute values
            maxValue = Math.max(Math.max(Math.abs(frontLeftMotorPower), Math.abs(frontRightMotorPower)),
                    Math.max(Math.abs(backLeftMotorPower), Math.abs(backRightMotorPower)));
        }
        //maxValue will normally be 1, so this just applies the power in that case
        //also apply the multiplier here
        frontLeftMotor.setPower (multiplier * (frontLeftMotorPower / maxValue));
        frontRightMotor.setPower(multiplier * (frontRightMotorPower / maxValue));
        backLeftMotor.setPower(multiplier * (backLeftMotorPower / maxValue));
        backRightMotor.setPower(multiplier * (backRightMotorPower / maxValue));
    }

    //stop method used for emergency stop - sets all motor powers to zero
    public void stop() {
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    //methods that involve other methods
    //moveOn is used to move along a specific line and from one angle to another
    public void moveOn(Line pathway, double angleChange, double power) {
        //first, make sure all our variables are set up
        //we're going to treat the robot as the origin to simplify some math
        Line adjustedPathway = pathway.startAtOrigin();
        //set a new endpoint to make things a bit quicker
        Point endpoint = adjustedPathway.getEndPoint();
        //update our ticks initially
        updateTicks();
        //we need a point to track the current position of the robot
        Point currentPosition = Point.origin();
        //generate an estimated current angle
        double currentAngle = 0;
        //run while the current position is not the endpoint and the angle is not the final angle
        while (!(currentPosition.equals(endpoint)) && !(MathUtilities.closeEnough(currentAngle, angleChange))){
            //first step - set the powers
            resetPowerValues();
            //TODO: Figure this out i'm braindead
        }

    }

    //update the previous tick values
    public void updateTicks() {
        //first set the previous ticks equal to the current ticks
        previousEncoderFL = getFrontLeftMotorTicks();
        previousEncoderFR = getFrontRightMotorTicks();
        previousEncoderBL = getBackLeftMotorTicks();
        previousEncoderBR = getBackRightMotorTicks();
    }

    //setters for only the booleans because using setters for other values will break things
    //even these setters will break things a little bit, but they should break things less
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public void setHalfSpeed(boolean halfSpeed) {
        this.halfSpeed = halfSpeed;
    }

    //getters
    public double getFrontLeftMotorPower() {
        return frontLeftMotorPower;
    }

    public double getFrontRightMotorPower() {
        return frontRightMotorPower;
    }

    public double getBackLeftMotorPower() {
        return backLeftMotorPower;
    }

    public double getBackRightMotorPower() {
        return backRightMotorPower;
    }

    public boolean isReverse() {
        return reverse;
    }

    public boolean isHalfSpeed() {
        return halfSpeed;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public int getFrontLeftMotorTicks() {
        return frontLeftMotor.getCurrentPosition();
    }

    public int getFrontRightMotorTicks() {
        return frontRightMotor.getCurrentPosition();
    }

    public int getBackLeftMotorTicks() {
        return backLeftMotor.getCurrentPosition();
    }

    public int getBackRightMotorTicks() {
        return backRightMotor.getCurrentPosition();
    }
}
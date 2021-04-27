/*This class should handle most of the direct setting of powers,
dealing with motors, hardware, etc. that is needed for movement

The autonomous portions are handled by robot since they use a lot of different sensors and stuff
 */



//JUST IN CASE I NEED IT FOR THIS MEETING
/*
            -Most prominent issues:
                Not point turns were very slow and difficult
                strafe was mostly good, but a little drifty
                slow and noisy
                inconsistent firing
                ESD
                got stuck on rings
                Driving aim
                polycord occasionally came off
                Don't destroy rings
                wobble goal arm


            -need auto that does more

            -good job:
                Picking up went well
                Didn't break anything
                No disconnects
                No pieces fell
                Hit powershots
                wobble goal during auto
             */
package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.math.MathUtilities;
import org.firstinspires.ftc.teamcode.math.Point;
import org.firstinspires.ftc.teamcode.pathing.RobotPosition;

import org.firstinspires.ftc.teamcode.Values;

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
    //orientation used for changing which way is forward
    private int orientation;
    //final values should be changed based on testing for the robot each year
    //multipliers for powers of motors
    private final double FRONT_LEFT_MOTOR_POWER;
    private final double FRONT_RIGHT_MOTOR_POWER;
    private final double BACK_LEFT_MOTOR_POWER;
    private final double BACK_RIGHT_MOTOR_POWER;
    //by default, have these at 1 and then measure the proportions for each wheel
    private final double FRONT_LEFT_TICK_MULTIPLIER;
    private final double FRONT_RIGHT_TICK_MULTIPLIER;
    private final double BACK_LEFT_TICK_MULTIPLIER;
    private final double BACK_RIGHT_TICK_MULTIPLIER;
    //multipliers for converting x and y distance as well as angular motion to ticks
    //just using 1 for now until further testing
    private final double X_DISTANCE_PER_TICK;
    private final double Y_DISTANCE_PER_TICK;
    private final double ANGLE_PER_TICK;
    //previous motor encoder values
    private int previousEncoderFL;
    private int previousEncoderFR;
    private int previousEncoderBL;
    private int previousEncoderBR;
    //change in encoder values
    private double changeInFL;
    private double changeInFR;
    private double changeInBL;
    private double changeInBR;
    //final value for accuracy of equals's for angles and linear
    private final double ANGLE_RANGE;
    private final double LINEAR_RANGE;


    //build a robot based on a hardware map and names of the motors
    public MecanumDrive(HardwareMap map, String frontLeftDrive, String frontRightDrive,
                        String backLeftDrive, String backRightDrive, double flPowerMultiplier,
                        double frPowerMultiplier, double blPowerMultiplier, double brPowerMultiplier,
                        double flTickMultiplier, double frTickMultiplier, double blTickMultiplier,
                        double brTickMultiplier, double xPerTick, double yPerTick, double anglePerTick,
                        double angleRange, double linearRange) {
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
        //orientation starts at 0
        orientation = 0;
        //final initialization
        FRONT_LEFT_MOTOR_POWER = flPowerMultiplier;
        FRONT_RIGHT_MOTOR_POWER = frPowerMultiplier;
        BACK_LEFT_MOTOR_POWER = blPowerMultiplier;
        BACK_RIGHT_MOTOR_POWER = brPowerMultiplier;
        //tick multipliers
        FRONT_LEFT_TICK_MULTIPLIER = flTickMultiplier;
        FRONT_RIGHT_TICK_MULTIPLIER = frTickMultiplier;
        BACK_LEFT_TICK_MULTIPLIER = blTickMultiplier;
        BACK_RIGHT_TICK_MULTIPLIER = brTickMultiplier;
        //units per tick
        X_DISTANCE_PER_TICK = xPerTick;
        Y_DISTANCE_PER_TICK = yPerTick;
        ANGLE_PER_TICK = anglePerTick;
        //ranges
        ANGLE_RANGE = angleRange;
        LINEAR_RANGE = linearRange;
        //motor set up stuff
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    //constructor used when only motor powers are non 1 (ie teleop doesn't use any auto methods)
    public MecanumDrive(HardwareMap map, String frontLeftDrive, String frontRightDrive,
                        String backLeftDrive, String backRightDrive, double flPowerMultiplier,
                        double frPowerMultiplier, double blPowerMultiplier, double brPowerMultiplier) {
        this(map, frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive, flPowerMultiplier,
                frPowerMultiplier, blPowerMultiplier, brPowerMultiplier, 1, 1, 1, 1, 1, 1, 1 / (double) Values.ticksPerDegree, 1, 1);
    }

    //constructor used when the finals are set to a default of 1
    public MecanumDrive(HardwareMap map, String frontLeftDrive, String frontRightDrive,
                        String backLeftDrive, String backRightDrive) {
        this(map, frontLeftDrive, frontRightDrive, backLeftDrive,
                backRightDrive, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 / (double) Values.ticksPerDegree, 10, 10);
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

    //for changing which direction is forward
    /* 0 = normal
    1 = right
    2 = back
    3 = left
     */
    public void orient(int orientation) {
        this.orientation = orientation;
    }

    //linear motion uses a point as a vector to move
    public void linearMove(Point direction, double power) {
        //if the direction is the origin, we can just not do anything here
        if (direction.equals(Point.origin())) {
            return;
        }
        //scale the direction vector to be no more magnitude than power and to adjust based on the orientation
        Point scaledDirection = direction.getUnitVector().rotateAboutOrigin(90 * orientation);
        //find the maximum value of sin+cos for the unit vector since it may be greater than 1
        double maxValue = Math.abs(scaledDirection.getX()) + Math.abs(scaledDirection.getY());
        //finish scaling the vector
        scaledDirection.scale(power);
        //set the powers - y - x for front left and back right, y + x for front right and back left
        frontLeftMotorPower += ((scaledDirection.getY() - scaledDirection.getX()) / maxValue);
        backRightMotorPower += ((scaledDirection.getY() - scaledDirection.getX()) / maxValue);
        frontRightMotorPower += ((scaledDirection.getY() + scaledDirection.getX()) / maxValue);
        backLeftMotorPower += ((scaledDirection.getY() + scaledDirection.getX()) / maxValue);
    }

    //radial motion used for turning only takes power as a variable since it's designed for point turns
    //left is negative, right is positive
    public void radialMove(double power) {
        //positive values go on left side of motors
        frontLeftMotorPower += power;
        backLeftMotorPower += power;
        //negative values go on right side of motors
        frontRightMotorPower -= power;
        backRightMotorPower -= power;
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
        frontLeftMotor.setPower (multiplier * (frontLeftMotorPower / maxValue) * FRONT_LEFT_MOTOR_POWER);
        frontRightMotor.setPower(multiplier * (frontRightMotorPower / maxValue) * FRONT_RIGHT_MOTOR_POWER);
        backLeftMotor.setPower(multiplier * (backLeftMotorPower / maxValue) * BACK_LEFT_MOTOR_POWER);
        backRightMotor.setPower(multiplier * (backRightMotorPower / maxValue) * BACK_RIGHT_MOTOR_POWER);
    }

    //stop method used for emergency stop - sets all motor powers to zero
    public void stop() {
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    public double[] moveOnTurnDrive(double[] startPosition, double[] targetPosition, double power, Telemetry tele) {
        return new double[] {};
    }

    //new move on simultaneous programmed from the ground up
    public double[] moveOnSimultaneous(double[] startPosition, double[] targetPosition, double power, Telemetry tele) {
        //figure out initial angle
        double angle = Math.toRadians(startPosition[2]);
        //get differences in x, y, and angle
        double differenceInX = targetPosition[0] - startPosition[0];
        double differenceInY = targetPosition[1] - startPosition[1];
        double differenceInAngle = MathUtilities.angleDifference(targetPosition[2], startPosition[2]);
        //the x and y need to be rotated by the initial angle
        double xMotion = differenceInX * Math.cos(angle) - differenceInY * Math.sin(angle);
        double yMotion = differenceInX * Math.sin(angle) + differenceInY * Math.cos(angle);
        double robotXMoved;
        double robotYMoved;
        double angleMoved;
        updateEncoderValues();
        //check if the positions are close enough to equal
        while (!(MathUtilities.within(LINEAR_RANGE, startPosition[0], targetPosition[0])
                && MathUtilities.within(LINEAR_RANGE, startPosition[1], targetPosition[1])
                && MathUtilities.within(ANGLE_RANGE, startPosition[2], targetPosition[2]))) {
            //movement piece
            resetPowerValues();
            linearMove(new Point(xMotion, yMotion), power);
            radialMove(Math.copySign(power, differenceInAngle));
            updateMotorPowers();
            //update the robot positions
            updateEncoderValues();
            robotXMoved = calculateChangeInX();
            robotYMoved = calculateChangeInY();
            angleMoved = calculateChangeInAngle();
            //now update the positions
            startPosition[0] += (robotXMoved * Math.cos(-1 * angle) - robotYMoved * Math.sin(-1 * angle));
            startPosition[1] += (robotXMoved * Math.sin(-1 * angle) + robotYMoved * Math.cos(-1 * angle));
            startPosition[2] += angleMoved;
            //update the values used for calculation
            angle = Math.toRadians(startPosition[2]);
            differenceInX = targetPosition[0] - startPosition[0];
            differenceInY = targetPosition[1] - startPosition[1];
            differenceInAngle = MathUtilities.angleDifference(targetPosition[2], startPosition[2]);
            xMotion = differenceInX * Math.cos(angle) - differenceInY * Math.sin(angle);
            yMotion = differenceInX * Math.sin(angle) + differenceInY * Math.cos(angle);
            //update telemetry
            tele.addData("CurrentX", startPosition[0]);
            tele.addData("CurrentY", startPosition[1]);
            tele.addData("CurrentAngle", startPosition[2]);
            tele.addData("TargetX", targetPosition[0]);
            tele.addData("TargetY", targetPosition[1]);
            tele.addData("TargetAngle", targetPosition[2]);
            tele.addData("flPower", this.getFrontLeftMotorPower());
            tele.addData("frPower", this.getFrontRightMotorPower());
            tele.addData("blPower", this.getBackLeftMotorPower());
            tele.addData("brPower", this.getBackRightMotorPower());
            tele.update();
        }
        this.stop();
        //return the estimated current position
        return startPosition;
    }
    //update all the values for the changes and previous values
    private void updateEncoderValues() {
        changeInFL = (this.getFrontLeftMotorTicks() - previousEncoderFL) * FRONT_LEFT_TICK_MULTIPLIER;
        changeInFR = (this.getFrontRightMotorTicks() - previousEncoderFR) * FRONT_RIGHT_TICK_MULTIPLIER;
        changeInBL = (this.getBackLeftMotorTicks() - previousEncoderBL) * BACK_LEFT_TICK_MULTIPLIER;
        changeInBR = (this.getBackRightMotorTicks() - previousEncoderBR) * BACK_RIGHT_TICK_MULTIPLIER;
        previousEncoderFL = this.getFrontLeftMotorTicks();
        previousEncoderFR = this.getFrontRightMotorTicks();
        previousEncoderBL = this.getBackLeftMotorTicks();
        previousEncoderBR = this.getBackRightMotorTicks();
    }

    //calculate the change in x position relative to robot
    private double calculateChangeInX() {
        //+, -, -, +
        return -1 * X_DISTANCE_PER_TICK * (changeInFL - changeInFR - changeInBL + changeInBR) / 4;
    }

    //calculate the change in y position relative to robot
    private double calculateChangeInY() {
        //all motors go in the same direction for this
        return Y_DISTANCE_PER_TICK * (changeInFL + changeInFR + changeInBL + changeInBR) / 4;
    }

    //calculate the change in angle relative to robot
    private double calculateChangeInAngle() {
        //+, -, +, -
        return ANGLE_PER_TICK * (changeInFL - changeInFR + changeInBL - changeInBR) / 4;
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

    public int getOrientation() {
        return orientation;
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
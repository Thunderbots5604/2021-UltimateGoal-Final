/*This class should handle most of the direct setting of powers,
dealing with motors, hardware, etc. that is needed for movement

The autonomous portions are handled by robot since they use a lot of different sensors and stuff
 */

package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robot.Robot;
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
    //final value for accuracy of equals's for angles and linear
    private final double ANGLE_RANGE;
    private final double LINEAR_RANGE;

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

    //methods that involve other methods
    //moveOn is used to move along a specific line and from one angle to another
    //TODO: get this to work. right now it sorta works but not really
    public void moveOnSimultaneous(RobotPosition startRobotPosition, RobotPosition endRobotPosition, double power, Telemetry REMOVELATER) {
        //for testing only
        int count = 0;
        //we need to generate a new end position and start position, where we start at (0,0) with
        //angle = 0 and determine what the same relative end position would be
        RobotPosition newStartRobotPosition = RobotPosition.zero();
        //find the new end position
        RobotPosition newEndRobotPosition = endRobotPosition.minus(startRobotPosition);
        //difference in angles
        double angleDifference = newEndRobotPosition.getAngle();
        //now we need to route from the zero position to the end position
        while (!newStartRobotPosition.equalsRange(newEndRobotPosition, LINEAR_RANGE, ANGLE_RANGE)) {
            //reset motor powers to start
            resetPowerValues();
            //determine the details about the angle
            if (!MathUtilities.within(ANGLE_RANGE, newStartRobotPosition.getAngle(), newEndRobotPosition.getAngle())) {
                //if the angles aren't close enough to be the same
                //here we branch into two: turning right vs turning left
                //first, make sure that the difference in angles is between -180 and 180
                REMOVELATER.addData("angles aren't the same", null);
                if (Math.abs(angleDifference) > 180) {
                    angleDifference -= Math.copySign(360, angleDifference);
                    REMOVELATER.addData("angle diff is greater than 180", null);
                }
                //now, we move left or right to try to match to the angle
                radialMove(Math.copySign(power, angleDifference));
            }
            //next, we need to determine the details about the linear motion
            if(!newStartRobotPosition.getLocation().equalsRange(newEndRobotPosition.getLocation(), LINEAR_RANGE)) {
                //just linear move on the end minus the start position
                linearMove(newEndRobotPosition.getLocation().minus(newStartRobotPosition.getLocation()), power);
                REMOVELATER.addData("location diff is real", null);
            }
            updateMotorPowers();
            //finally, we need to update the position
            updateTicks();
            newStartRobotPosition = newStartRobotPosition.plus(new RobotPosition(calculateChangeInLocation().rotateAboutOrigin(calculateChangeInAngle()), calculateChangeInAngle()));
            //for testing - update the telemetry with a count of how many times it has run
            count++;
            REMOVELATER.addData("Current Location X", newStartRobotPosition.getLocation().getX());
            REMOVELATER.addData("Current Location Y", newStartRobotPosition.getLocation().getY());
            REMOVELATER.addData("Angle", newStartRobotPosition.getAngle());
            REMOVELATER.addData("count", count);
            REMOVELATER.update();
        }
        stop();
    }

    //non simultaneous radial and linear combined motion
    public RobotPosition moveOn(RobotPosition startRobotPosition, RobotPosition endRobotPosition, double power, Telemetry REMOVELATER) {
        //we need to generate a new end position and start position, where we start at (0,0) with
        //angle = 0 and determine what the same relative end position would be
        RobotPosition newStartRobotPosition = RobotPosition.zero();
        //find the new end position
        RobotPosition newEndRobotPosition = endRobotPosition.minus(startRobotPosition);
        //difference in angles
        double angleDifference = newEndRobotPosition.getAngle();
        //update the tick values
        updateTicks();
        //determine the details about the angle
        while (!MathUtilities.within(ANGLE_RANGE, newStartRobotPosition.getAngle(), newEndRobotPosition.getAngle())) {
            //if the angles aren't close enough to be the same
            //reset motor powers to start
            resetPowerValues();
            //give some data to the telemetry
            if (angleDifference > 0) {
                REMOVELATER.addData("turning", "left");
            }
            else {
                REMOVELATER.addData("turning", "right");
            }
            REMOVELATER.addData("angles aren't the same", null);
            //now, we move left or right to try to match to the angle
            radialMove(Math.copySign(power, angleDifference));
            //update motor powers
            updateMotorPowers();
            //calculate the positions again
            newStartRobotPosition = newStartRobotPosition.minus(new RobotPosition(/*calculateChangeInLocation().rotateAboutOrigin(newStartRobotPosition.getAngle())*/Point.origin(), calculateChangeInAngle()));
            //update the ticks
            updateTicks();
            //current position
            REMOVELATER.addData("Angle", newStartRobotPosition.getAngle());
            REMOVELATER.addData("X Position", newStartRobotPosition.getLocation().getX());
            REMOVELATER.addData("Y Position", newStartRobotPosition.getLocation().getY());
            REMOVELATER.update();
        }
        stop();
        //next, we need to do linear motion

        while(!newStartRobotPosition.getLocation().equalsRange(newEndRobotPosition.getLocation(), LINEAR_RANGE)) {
            //reset the motor powers
            resetPowerValues();
            //just linear move on the end minus the start position
            linearMove(newEndRobotPosition.getLocation().minus(newStartRobotPosition.getLocation()).rotateAboutOrigin(newStartRobotPosition.getAngle()), power);
            REMOVELATER.addData("location diff is real", null);
            //update motor powers
            updateMotorPowers();
            //calculate the positions again
            newStartRobotPosition = newStartRobotPosition.minus(new RobotPosition(calculateChangeInLocation().rotateAboutOrigin(newStartRobotPosition.getAngle()), 0));
            //update the ticks
            updateTicks();
            //current position
            REMOVELATER.addData("Angle", newStartRobotPosition.getAngle());
            REMOVELATER.addData("X Position", newStartRobotPosition.getLocation().getX());
            REMOVELATER.addData("Y Position", newStartRobotPosition.getLocation().getY());
            REMOVELATER.update();
        }

        stop();
        return newStartRobotPosition;
    }

    //methods that involve other methods
    //moveOn is used to move along a specific line and from one angle to another
    public void test(RobotPosition startRobotPosition, RobotPosition endRobotPosition, double power, Telemetry REMOVELATER) {
        //for testing only
        int count = 0;
        //we need to generate a new end position and start position, where we start at (0,0) with
        //angle = 0 and determine what the same relative end position would be
        RobotPosition newStartRobotPosition = RobotPosition.zero();
        //find the new end position
        RobotPosition newEndRobotPosition = endRobotPosition.minus(startRobotPosition);
        //difference in angles
        double angleDifference = newEndRobotPosition.getAngle();
        //now we need to route from the zero position to the end position
        while (!newStartRobotPosition.equalsRange(newEndRobotPosition, LINEAR_RANGE, ANGLE_RANGE)) {
            //reset motor powers to start
            resetPowerValues();
            //determine the details about the angle
            if (!MathUtilities.within(ANGLE_RANGE, newStartRobotPosition.getAngle(), newEndRobotPosition.getAngle())) {
                //if the angles aren't close enough to be the same
                //here we branch into two: turning right vs turning left
                //first, make sure that the difference in angles is between -180 and 180
                REMOVELATER.addData("angles aren't the same", null);
                if (Math.abs(angleDifference) > 180) {
                    angleDifference -= Math.copySign(360, angleDifference);
                    REMOVELATER.addData("angle diff is greater than 180", null);
                }
                //now, we move left or right to try to match to the angle
                radialMove(Math.copySign(power, angleDifference));
            }
            //next, we need to determine the details about the linear motion
            if(!newStartRobotPosition.getLocation().equalsRange(newEndRobotPosition.getLocation(), LINEAR_RANGE)) {
                //just linear move on the end minus the start position
                linearMove(newEndRobotPosition.getLocation().minus(newStartRobotPosition.getLocation()), power);
                REMOVELATER.addData("location diff is real", null);
            }
            updateMotorPowers();
            //finally, we need to update the position
            updateTicks();
            newStartRobotPosition = newStartRobotPosition.minus(new RobotPosition(calculateChangeInLocation(), calculateChangeInAngle()));
            //for testing - update the telemetry with a count of how many times it has run
            count++;
            REMOVELATER.addData("Current Location X", newStartRobotPosition.getLocation().getX());
            REMOVELATER.addData("Current Location Y", newStartRobotPosition.getLocation().getY());
            REMOVELATER.addData("Angle", newStartRobotPosition.getAngle());
            REMOVELATER.addData("count", count);
            REMOVELATER.update();
        }
        stop();
    }

    public RobotPosition endMe(RobotPosition startRobotPosition, RobotPosition endRobotPosition, double power, Telemetry REMOVELATER) {
        //for testing only
        int count = 0;
        //we need to generate a new end position and start position, where we start at (0,0) with
        //angle = 0 and determine what the same relative end position would be
        RobotPosition newStartRobotPosition = RobotPosition.zero();
        //find the new end position
        RobotPosition newEndRobotPosition = endRobotPosition.minus(startRobotPosition);
        newEndRobotPosition = new RobotPosition(new Point(newEndRobotPosition.getLocation().getY(), newEndRobotPosition.getLocation().getX()), newEndRobotPosition.getAngle());
        //difference in angles
        updateTicks();
        double angleDifference = newStartRobotPosition.getAngle();
        while (!MathUtilities.within(ANGLE_RANGE, newStartRobotPosition.getAngle(), newEndRobotPosition.getAngle())) {
            //if the angles aren't close enough to be the same
            //reset motor powers to start
            resetPowerValues();
            //give some data to the telemetry
            if (angleDifference > 0) {
                REMOVELATER.addData("turning", "left");
            }
            else {
                REMOVELATER.addData("turning", "right");
            }
            REMOVELATER.addData("angles aren't the same", null);
            //now, we move left or right to try to match to the angle
            radialMove(Math.copySign(power, angleDifference));
            //update motor powers
            updateMotorPowers();
            //calculate the positions again
            newStartRobotPosition = newStartRobotPosition.minus(new RobotPosition(Point.origin(), calculateChangeInAngle()));
            //update the ticks
            updateTicks();
            //current position
            REMOVELATER.addData("Angle", newStartRobotPosition.getAngle());
            REMOVELATER.addData("X Position", newStartRobotPosition.getLocation().getX());
            REMOVELATER.addData("Y Position", newStartRobotPosition.getLocation().getY());
            REMOVELATER.update();
        }
        newEndRobotPosition = new RobotPosition(newEndRobotPosition.getLocation().rotateAboutOrigin(newEndRobotPosition.getAngle()), 0);
        //now we need to route from the zero position to the end position
        while (!newStartRobotPosition.getLocation().equalsRange(newEndRobotPosition.getLocation(), LINEAR_RANGE)) {
            resetPowerValues();
            //just linear move on the end minus the start position
            linearMove(newEndRobotPosition.getLocation().minus(newStartRobotPosition.getLocation()), power);
            REMOVELATER.addData("location diff is real", null);
            updateMotorPowers();
            //finally, we need to update the position
            updateTicks();
            newStartRobotPosition = newStartRobotPosition.minus(new RobotPosition(calculateChangeInLocation(), calculateChangeInAngle()));
            //for testing - update the telemetry with a count of how many times it has run
            count++;
            REMOVELATER.addData("Current Location X", newStartRobotPosition.getLocation().getX());
            REMOVELATER.addData("Current Location Y", newStartRobotPosition.getLocation().getY());
            REMOVELATER.addData("Angle", newStartRobotPosition.getAngle());
            REMOVELATER.addData("count", count);
            REMOVELATER.update();
        }
        stop();
        return newStartRobotPosition;
    }

    //update the previous tick values
    public void updateTicks() {
        //first set the previous ticks equal to the current ticks
        previousEncoderFL = getFrontLeftMotorTicks();
        previousEncoderFR = getFrontRightMotorTicks();
        previousEncoderBL = getBackLeftMotorTicks();
        previousEncoderBR = getBackRightMotorTicks();
    }

    //calculate the change in location based on encoder ticks
    public Point calculateChangeInLocation() {
        //first we find the change in ticks
        //don't need back left change
        double changeInTicksFL = (getFrontLeftMotorTicks() - previousEncoderFL) * FRONT_LEFT_TICK_MULTIPLIER;
        double changeInTicksFR = (getFrontRightMotorTicks() - previousEncoderFR) * FRONT_RIGHT_TICK_MULTIPLIER;
        double changeInTicksBR = (getBackRightMotorTicks() - previousEncoderBR) * BACK_RIGHT_TICK_MULTIPLIER;
        /*this is an approximation, so we can just use expected motion
        since actual motion takes far too long to find*/
        /*After doing the algebra, assuming correct motion, we get
        x ~ (FR-FL)/2
        y ~ (FR+BR)/2
         */
        //change in x
        double changeInX = X_DISTANCE_PER_TICK * -1 * ((changeInTicksFR - changeInTicksFL) / 2);
        //change in y
        double changeInY = Y_DISTANCE_PER_TICK * -1 * ((changeInTicksFR + changeInTicksBR) / 2);
        return new Point(changeInX, changeInY);
    }

    //calculate the change in angle based on encoder ticks
    public double calculateChangeInAngle() {
        //first we find the change in ticks
        //don't need anything except FR and BL for this one
        double changeInTicksFR = (getFrontRightMotorTicks() - previousEncoderFR) * FRONT_RIGHT_TICK_MULTIPLIER;
        double changeInTicksBL = (getBackLeftMotorTicks() - previousEncoderBL) * BACK_LEFT_TICK_MULTIPLIER;
        /* algebra says that correct motion will give
        turning ~ (FR-BL)/2
         */
        return ANGLE_PER_TICK * (changeInTicksFR - changeInTicksBL) / 2;

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
/*This class should handle most of the direct setting of powers,
dealing with motors, hardware, etc. that is needed for movement
 */

//TODO: make this used for autonomous as well as teleop, along with hopefully expanding features to
//TODO: have an arbitrary number and type of components that can each get their methods passed
//          that one is going to be real hard btw

package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

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


    //build a robot based on a hardware map and names of the motors
    public MecanumDrive(HardwareMap map, String frontLeftDrive, String frontRightDrive,
                        String backLeftDrive, String backRightDrive) {
        this.map = map;
        //build the drive system
        frontLeftMotor = map.get(DcMotorEx.class, frontLeftDrive);
        frontRightMotor = map.get(DcMotorEx.class, frontRightDrive);
        backLeftMotor = map.get(DcMotorEx.class, backLeftDrive);
        backRightMotor = map.get(DcMotorEx.class, backRightDrive);
        //motor set up stuff
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    //include some default strings for lovable dummies that aren't gonna want to type
    //all of the names in manually
    public MecanumDrive(HardwareMap map) {
        this(map, "lmf", "lmb", "rmf", "rmb");
    }

    //linear motion uses a point as a vector to move
    public void linearMove(Point vector) {

    }

    //stop method sets all motor powers to zero
    public void stop() {
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }
}

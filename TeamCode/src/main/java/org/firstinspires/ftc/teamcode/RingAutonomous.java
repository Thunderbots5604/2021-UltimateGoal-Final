/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.pathing.RobotPosition;
import org.firstinspires.ftc.teamcode.math.Point;

@Autonomous(name="Ring", group="")
public class RingAutonomous extends LinearOpMode {

    private Cam cam = new Cam(telemetry, hardwareMap);
    private Move move = new Move(telemetry, this);
    private Others motors = new Others(telemetry);
    private Gyro gyro = new Gyro();

    private int halfOfField = Values.halfOfField;
    private int tileLength = Values.tileLength;
    private double power = Values.power;
    private double[] coords = {20, -50, 0};

    private DcMotor intake = null;
    private DcMotor flyWheel = null;
    private DcMotor feeder = null;

    @Override
    public void runOpMode() {
        MecanumDrive mecanumDrive = new MecanumDrive(hardwareMap);
        intake = hardwareMap.get(DcMotor.class, "intake");
        flyWheel = hardwareMap.get(DcMotor.class, "out");
        feeder = hardwareMap.get(DcMotor.class, "feed");

        move.initialize(hardwareMap);
        motors.initMotors(hardwareMap);
        cam.initVuforia(hardwareMap);
        gyro.initGyro(hardwareMap);
        cam.startCam();
        int zone = cam.getZone();
        cam.endCam();
        waitForStart();
        motors.rotateCamPlatform(.9);

        //Shoot Rings
        mecanumDrive.moveOnSimultaneous(coords, new double[] {30, -3, 0}, power, telemetry);
        motors.shootPower(-2000);
        motors.getRing();
        motors.fireRing(1);
        move.move(0, power, "gyro turn");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {25, -3, 0}, power, telemetry);
        sleep(1000);
        motors.fireRing(1);
        motors.shootPower(-2100);
        move.move(0, power, "gyro turn");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {20, -3, 0}, power, telemetry);
        sleep(1000);
        motors.fireRing(1);
        motors.shootPower(0);

        //Drop Wobble
        move.move(0, power, "gyro turn");
        updateCoord();
        if (zone == 0) {
            mecanumDrive.moveOnSimultaneous(coords, new double[] {55, 0, 0}, power, telemetry);
        }
        else if (zone == 1) {
            mecanumDrive.moveOnSimultaneous(coords, new double[] {35, 15, 0}, power, telemetry);
        }
        else {
            mecanumDrive.moveOnSimultaneous(coords, new double[] {55, 30, 0}, power, telemetry);
        }

        motors.dropArm();
        sleep(1000);
        motors.resetArm();
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {45, -3, 0}, power, telemetry);
        move.move(0, power, "gyro turn");

        //Pick up rings on field
        if (zone != 0) {
            motors.getRing();
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {40, -20}, power, telemetry);
            sleep(500);
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {40, -25}, power, telemetry);
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {33, -3}, power, telemetry);
            move.move(0, power, "gyro turn");
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {33, -3 , 0}, power, telemetry);

            //Fire
            motors.shootPower(-2200);
            motors.fireRing(zone * 2 - 1);
            motors.shootPower(0);
            motors.ringLG();
        }
        else {
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {55, -50, 0}, power, telemetry);
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {35, -50, 0}, power, telemetry);
            move.move(10, power, "turn left");

            mecanumDrive.moveOnSimultaneous(coords, new double[] {55, 0, 0}, power, telemetry);
        }

        //Park
        move.move(0, power, "gyro turn");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {50, 8, 0}, power, telemetry);
        Values.finalAngle = gyro.getAngle();
    }

    public void updateCoord() {
        if (cam.getCoords()) {
            coords = Values.currentCoords;
        }
        else {
            coords[2] = gyro.getAngle();
        }
    }
}

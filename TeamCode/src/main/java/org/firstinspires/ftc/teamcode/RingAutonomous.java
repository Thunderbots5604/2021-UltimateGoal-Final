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
    private double[] coords = {Values.X_START,Values.Y_START, 0};
    private double[] zones = new double[3];

    @Override
    public void runOpMode() {
        MecanumDrive mecanumDrive = new MecanumDrive(hardwareMap);

        move.initialize(hardwareMap);
        motors.initMotors(hardwareMap);
        cam.initVuforia(hardwareMap);
        gyro.initGyro(hardwareMap);
        cam.startCam();
        int zone = cam.getZone();
        if (zone == 0) {
            zones = new double[] {-5, 55, 0};
        }
        else if (zone == 1) {
            zones = new double[] {15, 35, 0};
        }
        else {
            zones = new double[] {35, 55, 0};
        }
        cam.endCam();
        waitForStart();
        motors.rotateCamPlatform(.9);

        //Shoot Rings
        mecanumDrive.moveOnSimultaneous(coords, new double[] {-3, 30, 0}, power, telemetry);
        motors.shootPower(-2000);
        motors.getRing();
        motors.fireRing(1);
        move.move(0, power, "gyro turn");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {-3, 25, 0}, power, telemetry);
        sleep(1000);
        motors.fireRing(1);
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {-3, 20, 0}, power, telemetry);
        sleep(1000);
        motors.fireRing(1);
        motors.shootPower(0);

        //Drop Wobble
        move.move(0, power, "gyro turn");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, zones, power, telemetry);

        motors.dropArm();
        sleep(1000);
        motors.resetArm();
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {-3, 35, 0}, power, telemetry);
        move.move(0, power, "gyro turn");

        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {-50, 55, 0}, power, telemetry);
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {-50, 35, 0}, power, telemetry);
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, zones, power, telemetry);
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {-20, 35, 0}, power, telemetry);

        //Park
        move.move(0, power, "gyro turn");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {8, 50, 0}, power, telemetry);
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
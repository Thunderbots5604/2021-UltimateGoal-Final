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
    private Others motors = new Others(telemetry, this);
    private Gyro gyro = new Gyro();

    private int halfOfField = Values.halfOfField;
    private int tileLength = Values.tileLength;
    private double power = Values.power;
    private double[] coords = {Values.X_START,Values.Y_START, 0};
    private double[] zones = new double[3];

    @Override
    public void runOpMode() {
        MecanumDrive mecanumDrive = new MecanumDrive(hardwareMap, "lmf", "rmf", "lmb", "rmb", 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 / Values.TICKS_PER_ANGLE, 5, 10);

        move.initialize(hardwareMap);
        cam.initVuforia(hardwareMap);
        gyro.initGyro(hardwareMap);
        cam.startCam();
        int zone = cam.getZone();
        if (zone == 0) {
            zones = new double[] {2000, -600, 0};
        }
        else if (zone == 1) {
            zones = new double[] {1100, 300, 0};
        }
        else {
            zones = new double[] {2000, 1150, 0};
        }
        telemetry.addData("Zone: ", zone);
        telemetry.update();
        cam.endCam();
        motors.initMotors(hardwareMap);
        waitForStart();
        motors.rotateCamPlatform(.33);

        //Shoot Rings
        mecanumDrive.moveOnSimultaneous(coords, new double[] {1100, -250, 0}, power * .8, telemetry);
        motors.shootPower(-2060);
        motors.getRing();
        sleep(1000);
        move.move(355, power, "gyro turn");
        motors.fireRing(1);
        move.move(5, power, "turn left");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {800, -250, 0}, power, telemetry);
        sleep(1000);
        move.move(355, power, "gyro turn");
        motors.fireRing(1);
        move.move(5, power, "turn left");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {550, -250, 0}, power, telemetry);
        sleep(1000);
        move.move(355, power, "gyro turn");
        motors.fireRing(1);
        motors.shootPower(0);
        motors.ringLG();

        //Drop Wobble
        move.move(0, power, "gyro turn");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, zones, power, telemetry);
        sleep(100);
        move.move(0, power, "gyro turn");
        sleep(1000);
        move.move(0, power, "gyro turn");

        motors.dropArm();
        mecanumDrive.moveOnSimultaneous(coords, new double[]{zones[0], zones[1], 0}, power, telemetry);
        motors.resetArm();

        //Go to Second Wobble
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {2200, -700, 0}, power, telemetry);
        sleep(500);
        move.move(0, power, "gyro turn");
        mecanumDrive.moveOnSimultaneous(coords, new double[] {2200, -1800, 0}, power, telemetry);
        sleep(200);
        move.move(0, power, "gyro turn");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {1900, -1800, 0}, power, telemetry);
        sleep(100);
        mecanumDrive.moveOnSimultaneous(coords, new double[] {1950, -500, 0}, power, telemetry);
        sleep(100);
        mecanumDrive.moveOnSimultaneous(coords, new double[] {zones[0], zones[1] - 100, 0}, power, telemetry);
        sleep(100);

        //Park
        move.move(0, power, "gyro turn");
        if (zone == 0) {
            move.move(300, power, "backward");
            move.move(1000, power, "strafe right");
            move.parkToWhite(hardwareMap, true);
        }
        else {
            move.parkToWhite(hardwareMap, false);
        }
        Values.finalAngle = gyro.getAngle();
    }

    public void updateCoord() {
        /*if (cam.getCoords()) {
            coords = Values.currentCoords;
        }
        else {*/
        coords[2] = gyro.getAngle();
        //}
        if (coords[2] > 180) {
            coords[2] -= 360;
        }
    }
}
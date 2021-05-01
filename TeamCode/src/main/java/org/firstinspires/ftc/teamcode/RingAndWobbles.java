/*
Back up a bit on the ring shoot
(For zone 1 at least) go a bit lekss left when getting second wobble goal
*/


package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;

@Autonomous(name="Ring+2Wobble", group="")
public class RingAndWobbles extends LinearOpMode {

    private Cam cam = new Cam(telemetry, hardwareMap);
    private Move move = new Move(telemetry, this);
    private Others motors = new Others(telemetry, this);
    private Gyro gyro = new Gyro();

    private int halfOfField = Values.halfOfField;
    private int tileLength = Values.tileLength;
    private double power = Values.power;
    private double[] coords = {Values.X_START + 600,Values.Y_START, 0};
    private double[] zones = new double[3];

    @Override
    public void runOpMode() {
        MecanumDrive mecanumDrive = new MecanumDrive(hardwareMap, "lmf", "rmf", "lmb", "rmb", 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 / Values.TICKS_PER_ANGLE, 3, 10);

        move.initialize(hardwareMap);
        cam.initVuforia(hardwareMap);
        gyro.initGyro(hardwareMap);
        cam.startCam();
        int zone = cam.getZone();
        if (zone == 0) {
            zones = new double[] {coords[0], -250, 0};
        }
        else if (zone == 1) {
            zones = new double[] {coords[0] - 660, 490, 0};
        }
        else {
            zones = new double[] {coords[0], 1000, 0};
        }
        telemetry.addData("Zone: ", zone);
        telemetry.update();
        cam.endCam();
        motors.initMotors(hardwareMap);
        waitForStart();

        //Drop Wobble
        move.move(0, power, "gyro turn");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, zones, power, telemetry);
        sleep(100);
        move.move(0, power, "gyro turn");
        if (zone == 1) {
            coords[0] += 300;
        }
        if (zone == 2) {
            coords[0] += 100;
        }

        motors.dropArm();
        updateCoord();
        sleep(200);
        motors.resetArm();

        //Shoot Rings
        updateCoord();

        mecanumDrive.moveOnSimultaneous(coords, new double[] {400, coords[1], 0}, power * .9, telemetry);

        coords[1] += 50;
        motors.shootPower(-2150);
        sleep(200);
        move.move(0, power, "gyro turn");
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {350, -500, 0}, power * .9, telemetry);
        move.move(0, power, "gyro turn");
        sleep(700);
        move.move(0, power, "gyro turn");
        motors.fireRing(1);
        motors.getRing();
        motors.shootPower(-2070);
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {100, -500, 0}, power, telemetry);
        sleep(200);
        move.move(0, power, "gyro turn");
        motors.fireRing(1);
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {-100, -580, 0}, power, telemetry);
        sleep(200);
        move.move(0, power, "gyro turn");
        motors.fireRing(1);
        motors.shootPower(0);
        motors.ringLG();

        if (zone == 1) {
            coords[0] -= 150;
        }

        //Go to Second Wobble
        updateCoord();
        mecanumDrive.moveOnSimultaneous(coords, new double[] {200, -1750, 0}, power, telemetry);
        sleep(100);
        mecanumDrive.moveOnSimultaneous(coords, new double[] {400, -2000, 0}, power * .7, telemetry);
        sleep(500);
        move.move(0, power, "gyro turn");
        move.move(40, power, "forward");
        mecanumDrive.moveOnSimultaneous(coords, new double[] {580, coords[1], 0}, power, telemetry);
        sleep(200);
        move.move(0, power, "gyro turn");
        updateCoord();

        //Drop Second Wobble
        if (zone == 0) {
            mecanumDrive.moveOnSimultaneous(coords, new double[] {700, -1300, 20}, power * .7, telemetry);
            sleep(100);
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {coords[0] + 100, -1000, 40}, power * .8, telemetry);
            move.move(40, power, "gyro turn");
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {zones[0] - 220, zones[1] - 50, coords[2]}, power * .7, telemetry);
            sleep(100);
            mecanumDrive.moveOnSimultaneous(coords, new double[] {coords[0] - 600, coords[1] - 300, coords[2]}, power, telemetry);
            move.move(0, power, "gyro turn");
        }
        else {
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {coords[0], -100, 0}, power * .7, telemetry);
            sleep(100);
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {coords[0], coords[1], 30}, power, telemetry);
            sleep(100);
            updateCoord();
            mecanumDrive.moveOnSimultaneous(coords, new double[] {coords[0], coords[1] + 300, 40}, power * .9, telemetry);
            updateCoord();
            sleep(100);
            mecanumDrive.moveOnSimultaneous(coords, new double[] {zones[0] - 50, zones[1] - 300, coords[2]}, power * .8, telemetry);
        }

        //Park
        move.move(0, power, "gyro turn");
        coords[2] = 0;
        mecanumDrive.moveOnSimultaneous(coords, new double[] {coords[0], -50, coords[2]}, power, telemetry);
        Values.finalAngle = gyro.getAngle();
        motors.dropArm();
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
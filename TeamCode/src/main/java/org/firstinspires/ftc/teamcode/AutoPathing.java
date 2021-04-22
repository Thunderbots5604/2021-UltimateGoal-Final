/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Spencer", group="")
public class AutoPathing extends LinearOpMode {

    Cam cam = new Cam(telemetry, hardwareMap);
    Move move = new Move(telemetry, this);
    Others motors = new Others(telemetry);
    Gyro gyro = new Gyro();

    int halfOfField = Values.halfOfField;
    int tileLength = Values.tileLength;
    double power = Values.power;
    double[] coords = {0, 0, 0};

    @Override
    public void runOpMode() {
        move.initialize(hardwareMap);
        motors.initMotors(hardwareMap);
        cam.initVuforia(hardwareMap);
        gyro.initGyro(hardwareMap);
        cam.startCam();
        int zone = cam.getZone();
        cam.endCam();
        waitForStart();

        move.move(tileLength * 3 + 225, power, "forward");
        motors.shootPower(-2200);
        motors.getRing();
        motors.fireRing(2);
        motors.shootPower(0);
        sleep(1000);
        motors.shootPower(-2200);
        motors.fireRing(1);
        motors.shootPower(0);
        move.move(tileLength, power, "forward");
        motors.dropArm();
        move.move(300, power, "backward");
        /*
        if (zone == 0) {
            move.move(tileLength * 2, power, "strafe left");
        }
        else if (zone == 1) {
            move.move(tileLength, power, "strafe left");
            move.move(tileLength, power, "forward");
        }
        else {
            move.move(tileLength * 2, power, "strafe left");
            move.move(tileLength * 2, power, "forward");
        }
        motors.dropArm();
        sleep(1500);
        motors.resetArm();
        if (zone == 0) {
            move.move(tileLength, power, "strafe right");
            move.move(tileLength, power, "forward");
        }
        move.parkToWhite(hardwareMap, false);
        */

        /*//Drop Wobble Goal on zone
        move.move(halfOfField, power, "forward");
        if (zone == 1) {
            move.move(20, power, "turn right");
        }
        move.move(tileLength * zone, power, "forward");
        motors.dropArm();

        //Reset to Center
        move.move(tileLength * zone, power, "backward");
        move.move(0, power, "gyro turn");
        move.move(tileLength, power, "backward");

        //Go For PowerShot
        motors.getRing();
        move.move(tileLength * 2, power, "strafe right");
        motors.shootPower(-2240);
        motors.fireRing(3);
        motors.shootPower(0);

        //Pick up rings on field
        if (zone != 0) {
            sleep(500);
            move.move(0, power, "gyro turn");
            move.move(tileLength, power, "forward");
            move.move(130, power, "turn right");
            move.move(0, power, "gyro turn");

            //Fire
            move.move(tileLength, power, "forward");
            motors.shootPower(-2240);
            motors.fireRing(zone * 2 - 1);

            motors.ringLG();
            motors.shootPower(0);
        }

        //Park
        move.move(tileLength / 2, power, "forward");*/
    }
}

/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Pathing", group="")
public class AutoPathing extends LinearOpMode {

    Cam cam = new Cam(telemetry, hardwareMap);
    Move move = new Move(telemetry);
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

        cam.getCoords();
        coords = Values.currentCoords;

        //Drop Wobble Goal on zone
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
        move.move(tileLength, power, "strafe right");
        for (int i = 0; i < 3; i++) {
            move.move(10, power, "turn right");
            motors.fireRing();
        }

        //Pick up rings on field
        if (zone != 0) {
            sleep(500);
            move.move(0, power, "gyro turn");
            //Makes pickup spinning thing
            motors.getRing();
            move.move(tileLength, power, "forward");
            move.move(130, power, "turn right");
            move.move(0, power, "gyro turn");

            //Fire
            move.move(tileLength, power, "forward");
            motors.fireRing();
            motors.fireRing();
            motors.fireRing();

            motors.ringLG();
        }

        //Park
        move.move(tileLength / 2, power, "forward");
    }
}

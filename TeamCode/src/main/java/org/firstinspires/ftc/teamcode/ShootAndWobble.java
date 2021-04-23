/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Shoot+Wobble", group="")
public class ShootAndWobble extends LinearOpMode {

    Gyro gyro = new Gyro();
    Cam cam = new Cam(telemetry, hardwareMap);
    Move move = new Move(telemetry, this);
    Others motors = new Others(telemetry);

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

        move.move(tileLength * 2 + 100, power, "forward");
        motors.shootPower(-2000);
        motors.getRing();
        motors.fireRing(1);
        move.move(10, power, "turn right");
        sleep(1000);
        motors.fireRing(1);
        motors.shootPower(-2100);
        move.move(10, power,"turn right");
        sleep(1000);
        motors.fireRing(1);
        motors.shootPower(0);
        motors.ringLG();

        if (zone == 0) {
            move.move(tileLength * 3 / 2, power, "strafe left");
        }
        else if (zone == 1) {
            move.move(200, power, "strafe left");
            move.move(tileLength, power, "forward");
        }
        else {
            move.move(tileLength * 3 / 2, power, "strafe left");
            move.move(tileLength * 2, power, "forward");
        }
        motors.dropArm();
        sleep(1500);
        motors.resetArm();
        if (zone == 0) {
            move.move(tileLength * 3 / 2, power, "strafe right");
            move.move(tileLength, power, "forward");
        }
        sleep(500);
        move.parkToWhite(hardwareMap, false);
    }
}

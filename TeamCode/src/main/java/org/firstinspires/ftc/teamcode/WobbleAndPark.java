/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Wobble + Park", group="Competition")
public class WobbleAndPark extends LinearOpMode {

    Move move = new Move(hardwareMap, telemetry);
    Cam cam = new Cam(telemetry);
    Others motors = new Others(hardwareMap, telemetry);

    //Distance calculators (Easier to change since not using coordinates)
    int halfOfField = 2400;
    int tileLength = 700;

    @Override
    public void runOpMode() throws InterruptedException {

        //Init and find zone to go to
        cam.startCam();
        int zone = cam.getZone();
        move.initialize();
        motors.initMotors();
        cam.endCam();
        waitForStart();

        //Go To Zone
        move.move((int) (tileLength * 1.5), .4, "strafe left");
        move.move(halfOfField + (tileLength * zone), 0.6, "forward");
        //The multiplier is to make it only apply to zone 1 (The only one that is on the right side)
        move.move(tileLength * (-Math.abs(zone - 1) + 1), .5, "strafe right");

        //Drop Goal
        motors.dropWobbleGoal();

        //Readjust to face forward
        move.move(0, .5, "gyro turn");

        //Park
        move.move(tileLength, .4, "strafe left");
        move.move(tileLength * zone, .5, "backward");
    }
}

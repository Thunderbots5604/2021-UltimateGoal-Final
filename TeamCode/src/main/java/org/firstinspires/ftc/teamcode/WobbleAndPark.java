/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Wobble + Park", group="Competition")
public class WobbleAndPark extends LinearOpMode {

    Move move = new Move();
    Cam cam = new Cam(telemetry);

    //Distance calculators (Easier to change since not using coordinates)
    int halfOfField = 5000;
    int tileLength = 1000;

    @Override
    public void runOpMode() throws InterruptedException {
        cam.startCam();
        int zone = cam.getZone();
        move.initialize(hardwareMap, telemetry);
        cam.endCam();
        waitForStart();
        move.move(halfOfField + (tileLength * zone), 0.5, "forward");
        //dropWobbleGoal();
        move.move(tileLength, .4, "strafe left");
        move.move(tileLength * zone, .5, "backward");
    }
}

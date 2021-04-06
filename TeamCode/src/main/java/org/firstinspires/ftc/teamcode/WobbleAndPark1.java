/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Wobble (1) + Park", group="Competition")
public class WobbleAndPark1 extends LinearOpMode {

    Move move = new Move(telemetry);
    Cam cam = new Cam(telemetry);
    Others motors = new Others(telemetry);

    //Distance calculators (Easier to change since not using coordinates)
    int halfOfField = Values.halfOfField;
    int tileLength = Values.tileLength;
    double power = Values.power;

    @Override
    public void runOpMode() throws InterruptedException {

        //Init and find zone to go to
        cam.startCam();
        int zone = cam.getZone();
        move.initialize(hardwareMap);
        telemetry.addData("Zone: ", zone);
        telemetry.update();
        waitForStart();
        cam.endCam();
        //Go To Zone
        move.startToZone(zone);
        if (zone == 0) {
            move.move(tileLength, power, "backward");
            move.move(tileLength, power, "strafe right");
            move.move(1500, power, "forward");
            sleep(1000);
        }
        move.parkToWhite(hardwareMap, false);
        if (zone == 0) {
            move.move(300, power, "strafe left");
        }
    }
}

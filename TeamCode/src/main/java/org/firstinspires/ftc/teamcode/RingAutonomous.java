/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Ring", group="Competition")
public class RingAutonomous extends LinearOpMode {

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
        motors.initMotors(hardwareMap);
        cam.endCam();
        telemetry.addData("Rings detected: ", zone);
        telemetry.update();
        waitForStart();

        //Go To Zone
        move.startToZone(zone);

        //Go back to corner
        move.zoneToCorner(zone, true);

        //Go to next wobble and put it in zone
        move.secondWobble(zone);

    }
}

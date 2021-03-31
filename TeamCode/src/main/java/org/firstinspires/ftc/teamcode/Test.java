/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Test", group="")
public class Test extends LinearOpMode {

    Cam cam = new Cam(telemetry);
    Move move = new Move(telemetry);
    @Override
    public void runOpMode() {
        move.initialize(hardwareMap);
        cam.startCam();
        int zone = cam.getZone();
        cam.endCam();

        telemetry.addData("Rings detected: ", zone);
        telemetry.update();
        waitForStart();
    }
}

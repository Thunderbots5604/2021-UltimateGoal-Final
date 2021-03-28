/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Test", group="")
public class Test extends LinearOpMode {

    Cam cam = new Cam(telemetry);
    @Override
    public void runOpMode() {
        cam.startCam();
        waitForStart();
        telemetry.addData("Starting ", "Detection");
        telemetry.update();
        sleep(2000);
        int zone = cam.getZone();
        cam.endCam();
        telemetry.addData("Zone: ", zone);
        telemetry.update();
        sleep(5000);
    }
}

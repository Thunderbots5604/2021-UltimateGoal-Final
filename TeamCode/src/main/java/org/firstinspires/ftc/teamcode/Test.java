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
        cam.tfod();
    }
}

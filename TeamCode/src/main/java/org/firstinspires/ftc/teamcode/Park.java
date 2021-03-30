/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Park", group="Competition")
public class Park extends LinearOpMode {

    Move move = new Move(hardwareMap, telemetry);

    @Override
    public void runOpMode() throws InterruptedException {
        move.initialize();
        waitForStart();
        move.move(1000, .4, "strafe left");
        move.move(2000, 0.5, "forward");
    }
}

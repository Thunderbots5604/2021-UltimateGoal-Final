/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Park", group="Competition")
public class Park extends LinearOpMode {

    Move move = new Move(telemetry, this);

    @Override
    public void runOpMode() throws InterruptedException {
        move.initialize(hardwareMap);
        waitForStart();
        move.move(1000, .3, "strafe left");
        move.move(2000, .3, "forward");
    }
}

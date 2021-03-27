/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Park", group="")
public class Park extends LinearOpMode {

    Move move = new Move();

    @Override
    public void runOpMode() throws InterruptedException {
        move.initialize(hardwareMap, telemetry);
        waitForStart();
        move.move(900, 0.5, "forward");
    }
}

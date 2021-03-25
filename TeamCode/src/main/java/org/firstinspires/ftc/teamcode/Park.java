/*This class is used to park the robot on the line. It should also serve
* as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Park extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Move move = new Move();
        move.initialize();
        move.move(500, 0.5, "forward");
    }
}

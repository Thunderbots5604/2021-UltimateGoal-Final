package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "Test :(", group = "")
public class Test extends LinearOpMode {

    public Cam vuforia = new Cam();
    public Move move = new Move();

    @Override
    public void runOpMode() {

        waitForStart();
        move.move(1000, .5, "forward");
        move.move(1000, .5, "backward");
        move.move(10000, .5, "turn left");
        sleep(10000);
        move.move(10000000, .3, "forward");
    }
}
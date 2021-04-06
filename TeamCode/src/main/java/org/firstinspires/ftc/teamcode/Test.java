/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Test", group="")
public class Test extends LinearOpMode {

    Cam cam = new Cam(telemetry);
    Move move = new Move(telemetry);
    Others motors = new Others(telemetry);

    int halfOfField = Values.halfOfField;
    int tileLength = Values.tileLength;
    double power = Values.power;

    @Override
    public void runOpMode() {
        move.initialize(hardwareMap);
        motors.initMotors(hardwareMap);
        //motors.testReading();
        waitForStart();
        //move.forwardToBlue();
        motors.resetArm();
        sleep(5000);
        motors.dropArm();
        sleep(5000);
        motors.resetArm();
    }
}

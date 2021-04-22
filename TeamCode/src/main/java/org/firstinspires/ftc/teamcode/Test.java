/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Test", group="")
public class Test extends LinearOpMode {

    Cam cam = new Cam(telemetry, hardwareMap);
    Move move = new Move(telemetry, this);
    Others motors = new Others(telemetry);
    Gyro gyro = new Gyro();

    int halfOfField = Values.halfOfField;
    int tileLength = Values.tileLength;
    double power = Values.power;
    double[] coords = {0, 0, 0};

    @Override
    public void runOpMode() {
        motors.initMotors(hardwareMap);
        move.initialize(hardwareMap);
        cam.initVuforia(hardwareMap);
        gyro.initGyro(hardwareMap);
        waitForStart();
    }
}

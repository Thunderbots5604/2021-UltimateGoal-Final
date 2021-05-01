/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.pathing.RobotPosition;
import org.firstinspires.ftc.teamcode.math.Point;

@Autonomous(name="shoot and park", group="")
public class AutoShootPark extends LinearOpMode {

    Cam cam = new Cam(telemetry, hardwareMap);
    Move move = new Move(telemetry, this);
    Others motors = new Others(telemetry, this);
    Gyro gyro = new Gyro();


    int halfOfField = Values.halfOfField;
    int tileLength = Values.tileLength;
    double power = 0.4;
    double[] coords = {0, 0, 0};

    @Override
    public void runOpMode() {
        MecanumDrive drive = new MecanumDrive(hardwareMap);
        move.initialize(hardwareMap);
        cam.initVuforia(hardwareMap);
        gyro.initGyro(hardwareMap);
        waitForStart();

        //drive.endMe();
    }
}

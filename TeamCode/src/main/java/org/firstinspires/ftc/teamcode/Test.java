/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import android.telephony.mbms.MbmsErrors;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.pathing.RobotPosition;

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
    double[] position = {0, 0, 0};

    @Override
    public void runOpMode() {
        MecanumDrive mecanumDrive = new MecanumDrive(hardwareMap);
        motors.initMotors(hardwareMap);
        move.initialize(hardwareMap);
        cam.initVuforia(hardwareMap);
        gyro.initGyro(hardwareMap);
        waitForStart();
        mecanumDrive.moveOnSimultaneous(position, new double[]{100, 0, 0}, power, telemetry);
        while (opModeIsActive()) {
            cam.getCoords();
            coords = Values.currentCoords;
            telemetry.addData("X: ", coords[0]);
            telemetry.addData("Y: ", coords[1]);
            telemetry.addData("Angle: ", coords[2]);
            telemetry.addData("Pos X: ", position[0]);
            telemetry.addData("Pos Y: ", position[1]);
            telemetry.addData("Pos Angle: ", position[2]);
            telemetry.update();
        }
    }
}

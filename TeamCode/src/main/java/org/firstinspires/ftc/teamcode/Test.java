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
    Others motors = new Others(telemetry, this);
    Gyro gyro = new Gyro();

    int halfOfField = Values.halfOfField;
    int tileLength = Values.tileLength;
    double power = Values.power;
    double[] coords = {0, 0, 0};
    double[] position = {0, 0, 0};

    @Override
    public void runOpMode() {
        MecanumDrive mecanumDrive = new MecanumDrive(hardwareMap, "lmf", "rmf", "lmb", "rmb", 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 / Values.TICKS_PER_ANGLE, 10, 10);
        motors.initMotors(hardwareMap);
        move.initialize(hardwareMap);
        cam.initVuforia(hardwareMap);
        gyro.initGyro(hardwareMap);
        waitForStart();
        motors.rotateCamPlatform(.33);
        mecanumDrive.moveOnSimultaneous(position, new double[]{0, 1000, 0}, power, telemetry);
        move.move(0, power, "gyro turn");
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

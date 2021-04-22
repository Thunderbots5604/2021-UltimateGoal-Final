/*This class is used to park the robot on the line. It should also serve
 * as a way of templating basic autonomous for testing*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.pathing.RobotPosition;
import org.firstinspires.ftc.teamcode.math.Point;

@Autonomous(name="Ring", group="")
public class RingAutonomous extends LinearOpMode {

    private Cam cam = new Cam(telemetry, hardwareMap);
    private Move move = new Move(telemetry, this);
    private Others motors = new Others(telemetry);
    private Gyro gyro = new Gyro();

    private int halfOfField = Values.halfOfField;
    private int tileLength = Values.tileLength;
    private double power = Values.power;
    private double[] coords = {0, 0, 0};
    private RobotPosition positionChange = null;
    private RobotPosition oldPosition = new RobotPosition(new Point(40, 200),0);
    private RobotPosition currentPosition = null;

    private DcMotor intake = null;
    private DcMotor flyWheel = null;
    private DcMotor feeder = null;

    @Override
    public void runOpMode() {
        MecanumDrive mecanumDrive = new MecanumDrive(hardwareMap);
        intake = hardwareMap.get(DcMotor.class, "intake");
        flyWheel = hardwareMap.get(DcMotor.class, "out");
        feeder = hardwareMap.get(DcMotor.class, "feed");

        move.initialize(hardwareMap);
        motors.initMotors(hardwareMap);
        cam.initVuforia(hardwareMap);
        gyro.initGyro(hardwareMap);
        cam.startCam();
        int zone = cam.getZone();
        cam.endCam();
        waitForStart();

        cam.getCoords();
        coords = Values.currentCoords;
        //Drop Wobble Goal on zone
        if (zone == 0) {
            motors.rotateCamPlatform(.33);
            positionChange = mecanumDrive.endMe(oldPosition, new RobotPosition(new Point(50, 0), 0), power, telemetry);
        }
        else if (zone == 1) {
            positionChange = mecanumDrive.endMe(oldPosition, new RobotPosition(new Point(50, 0), 0), power, telemetry);
            updateCoord();
            positionChange = mecanumDrive.endMe(currentPosition, new RobotPosition(new Point(30, -20), 0), power, telemetry);
        }
        else {
            positionChange = mecanumDrive.endMe(oldPosition, new RobotPosition(new Point(-50, 0), 0), power, telemetry);
        }
        motors.dropArm();
        sleep(1500);

        //Reset to Center
        updateCoord();
        positionChange = mecanumDrive.endMe(currentPosition, new RobotPosition(new Point(30, 10), 0), power, telemetry);
        motors.rotateCamPlatform(.33);
        updateCoord();
        positionChange = mecanumDrive.endMe(currentPosition, new RobotPosition(new Point(-50, 10), -10), power, telemetry);

        //Go For PowerShot
        motors.getRing();
        motors.shootPower(-2200);
        motors.fireRing(3);
        move.move(0, power, "gyro turn");
        motors.shootPower(0);

        //Pick up rings on field
        if (zone != 0) {
            motors.getRing();
            updateCoord();
            positionChange = mecanumDrive.endMe(currentPosition, new RobotPosition(new Point(0, 70), 0), power, telemetry);
            motors.ringLG();
            updateCoord();
            positionChange = mecanumDrive.endMe(currentPosition, new RobotPosition(new Point(-50, 10), 0), power, telemetry);

            //Fire
            motors.shootPower(-2200);
            motors.fireRing(zone * 2 - 1);
            motors.shootPower(0);
            motors.ringLG();

        }

        //Park
        updateCoord();
        positionChange = mecanumDrive.endMe(currentPosition, new RobotPosition(new Point(-50, -10), 0), power, telemetry);
        Values.finalAngle = gyro.getAngle();
    }

    public void updateCoord() {
        //old position = oldPosition
        //currentPosition = oldPosition.plus(positionChange);
        if (cam.getCoords()) {
            coords = Values.currentCoords;
            currentPosition = new RobotPosition(new Point(coords[1], coords[0]), coords[2]);
        }
        else {
            currentPosition = oldPosition.plus(positionChange);
        }
        oldPosition = currentPosition;
    }
}

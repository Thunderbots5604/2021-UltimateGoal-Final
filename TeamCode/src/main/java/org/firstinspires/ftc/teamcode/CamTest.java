
package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.math.Point;

@TeleOp(name = "Cam Test", group = "Tele")
public class CamTest extends OpMode {

    private double multiplier = 1;
    private double quarterSpeed = multiplier * 0.25;

    private boolean pastX = false;
    private boolean pastA = false;
    private boolean pastBack = false;

    private MecanumDrive drive;

    private Cam cam = new Cam(telemetry, hardwareMap);
    private Gyro gyro = new Gyro();
    private double[] coords = {0, 0, 0};


    @Override
    public void init() {
        telemetry.addData("initializing", null);
        telemetry.update();

        cam.initVuforia(hardwareMap);

        drive = new MecanumDrive(hardwareMap, "lmf", "rmf", "lmb", "rmb", 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 10, 10);

        //Set Gamepad Deadzone
        gamepad1.setJoystickDeadzone(0.05f);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    public void start(){
        //drive.updateEncoderValues();
    }

    @Override
    public void loop() {

        cam.getCoords();
        coords = Values.currentCoords;

        //Reverse Toggle
        if(!pastX && gamepad1.x){
            drive.toggleReverse();
        }
        pastX = gamepad1.x;

        //Halfspeed Toggle
        if(gamepad1.a == false && pastA == true){
            drive.toggleHalfSpeed();
        }
        pastA = gamepad1.a;

        drive.calculateMultiplier();

        drive.resetPowerValues();
        if (gamepad1.left_stick_y != 0 || gamepad1.left_stick_x != 0 || gamepad1.right_stick_x != 0) {
            drive.linearMove(new Point(gamepad1.left_stick_x, gamepad1.left_stick_y), Math.sqrt(gamepad1.left_stick_x * gamepad1.left_stick_x + gamepad1.left_stick_y * gamepad1.left_stick_y));
            drive.radialMove(gamepad1.right_stick_x);
        }
        //Second Drive at Quarter Speed
        else if (gamepad2.left_stick_y != 0 || gamepad2.left_stick_x != 0 || gamepad2.right_stick_x != 0) {
            drive.linearMove(new Point(gamepad1.left_stick_x, gamepad1.left_stick_y), Math.sqrt(gamepad1.left_stick_x * gamepad1.left_stick_x + gamepad1.left_stick_y * gamepad1.left_stick_y) * .7 * quarterSpeed);
            drive.radialMove(gamepad1.right_stick_x * .7 * quarterSpeed);
        }
        drive.updateMotorPowers();

        if(gamepad2.back && !pastBack) {
            drive.updateEncoderValues();
        }
        pastBack = gamepad2.back;

        telemetry.addData("Front Right Motor = ", drive.getFrontRightMotorTicks());
        telemetry.addData("Front Left Motor = ", drive.getFrontLeftMotorTicks());
        telemetry.addData("Back Right Motor = ", drive.getBackRightMotorTicks());
        telemetry.addData("Back Left Motor = ", drive.getBackLeftMotorTicks());
        telemetry.addData("Estimated X", drive.calculateChangeInX());
        telemetry.addData("Estimated Y", drive.calculateChangeInY());
        telemetry.addData("Estimated Angle", drive.calculateChangeInAngle());
        telemetry.addData("x: ", coords[0]);
        telemetry.addData("y: ", coords[1]);
        telemetry.addData("Angle: ", coords[2]);
        telemetry.addData("Gyro Angle: ", cam.camAngle());
        telemetry.update();
    }

    public void stop() {
        drive.stop();
    }
}

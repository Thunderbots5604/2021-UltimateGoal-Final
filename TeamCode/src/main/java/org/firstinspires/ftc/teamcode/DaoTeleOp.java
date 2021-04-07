/*
CONTROLS
Left stick controls movement
Right stick controls turning
A toggles ring grabber
B is unallocated
X toggles the wobble lock
Y toggles the wobble arm
Left bumper moves the lift up
Right bumper moves the lift down
Back button slows the speed of the lift
Left trigger halves the speed of the movement
Right trigger doubles the speed of the movement
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.math.Point;

@TeleOp(name = "DaoTeleOp", group = "Tele")
public class DaoTeleOp extends OpMode {

    private DcMotor flMotor = null;
    private DcMotor frMotor = null;
    private DcMotor blMotor = null;
    private DcMotor brMotor = null;
    private DcMotor liftL = null;
    private DcMotor liftR = null;

    private Servo WobbleLocker;
    private Servo WobbleArm;
    private Servo RingArm;

    private ColorSensor frontColor;
    private ColorSensor backColor;

    private double powerFRBL = 0;
    private double powerFLBR = 0;
    private double multiplier = 1;
    private double leftTrigger = 0;
    private double rightTrigger = 0;
    private double quarterSpeed = multiplier * 0.25;

    private boolean reverse = false;
    private boolean ring = false;
    private boolean pastX = false;
    private boolean secondX = false;
    private boolean pastY = false;
    private boolean pastB = false;
    private boolean pastA = false;
    private boolean secondA = false;
    private boolean pastLB = false;
    private boolean engageArm = false;
    private boolean engageLock = false;
    private boolean engageRing = false;
    private boolean halfSpeed = false;

    private MecanumDrive drive;

    @Override
    public void init() {
        //Get Hardware Map
        drive = new MecanumDrive(hardwareMap, "lmf", "rmf", "lmb", "rmb");

        drive.resetPowerValues();

        telemetry.addData("lmf power", drive.getFrontLeftMotorPower());
        telemetry.addData("rmf power", drive.getFrontRightMotorPower());
        telemetry.addData("lmb power", drive.getBackLeftMotorPower());
        telemetry.addData("rmb power", drive.getBackRightMotorPower());

        liftL = hardwareMap.get(DcMotor.class, "LiftMechL");
        liftR = hardwareMap.get(DcMotor.class, "LiftMechR");

        WobbleLocker = hardwareMap.get(Servo.class, "WobbleLocker" );
        RingArm = hardwareMap.get(Servo.class, "RingGrabber");
        WobbleArm = hardwareMap.get(Servo.class, "WobbleArm");

        frontColor = hardwareMap.get(ColorSensor.class, "fc");
        backColor = hardwareMap.get(ColorSensor.class, "bc");

        liftL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        liftL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Set Gamepad Deadzone
        gamepad1.setJoystickDeadzone(0.05f);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        drive.resetPowerValues();
        cheat(gamepad1.left_stick_x, gamepad1.left_stick_y);
        drive.radialMove(gamepad1.right_stick_x * multiplier * 0.25);
        drive.updateMotorPowers();
        telemetry.addData("lmf power", drive.getFrontLeftMotorPower());
        telemetry.addData("rmf power", drive.getFrontRightMotorPower());
        telemetry.addData("lmb power", drive.getBackLeftMotorPower());
        telemetry.addData("rmb power", drive.getBackRightMotorPower());

        //WobbleLocker
        if(!pastX && gamepad1.x){
            engageLock = !engageLock;
            if(engageLock){
                WobbleLocker.setPosition(0.4);
            }
            else{
                WobbleLocker.setPosition(0.5);
            }
        }
        pastX = gamepad1.x;

        //Lift Mechanism Up and Down
        if(gamepad1.left_bumper && gamepad1.back && ((liftL.getCurrentPosition() < -5 && liftR.getCurrentPosition() < -5) || gamepad1.right_bumper)){
            liftL.setPower(0.1);
            liftR.setPower(0.1);
        } else if (gamepad1.left_bumper && ((liftL.getCurrentPosition() < -5 && liftR.getCurrentPosition() < -5) || gamepad1.right_bumper)) {
            liftL.setPower(0.5);
            liftR.setPower(0.5);
        } else if (gamepad1.right_bumper && gamepad1.back) {
            liftL.setPower(-0.1);
            liftR.setPower(-0.1);
        } else if (gamepad1.right_bumper) {
            liftL.setPower(-0.5);
            liftR.setPower(-0.5);
        } else {
            liftL.setPower(0);
            liftR.setPower(0);
        }
        //Speed Toggle
        if (gamepad1.left_trigger > 0.8){
            multiplier = 0.5;
        } else if (gamepad1.right_trigger > 0.8) {
            multiplier = 2;
        } else {
            multiplier = 1;
        }
        //Wobble Arm Toggle
        if(gamepad1.y && !pastY){
            engageArm = !engageArm;
            if(engageArm){
                WobbleArm.setPosition(1);
            }
            else{
                WobbleArm.setPosition(0);
            }
        }
        pastY = gamepad1.y;

        //Ring Grabbing Toggle
        if(gamepad1.a && !secondA){
            ring = !ring;
            if(ring){
                RingArm.setPosition(1);
            }
            else{
                RingArm.setPosition(0.6);
            }
        }
        secondA = gamepad1.a;
        telemetry.addData("Swap mode = ", String.valueOf(gamepad1.back));
        telemetry.addData("LiftMech = ", liftL.getCurrentPosition());
        telemetry.addData("Arm Servo = ", WobbleArm.getPosition());
        telemetry.addData("Ring Arm = ", ring);
        telemetry.addData("WobbleLocker", engageLock);
        telemetry.update();
    }
    private void cheat(double x, double y) {
        if (x == 0 && y == 0) {
            return;
        }
        Point direction = new Point(x, y);
        double power = Math.sqrt(x*x + y*y) * 0.25 * multiplier;
        drive.linearMove(direction, power);
    }

    @Override
    public void stop() {
        drive.stop();
    }
}

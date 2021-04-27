package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.math.Point;

@TeleOp(name = "TeleOpKrab", group = "Tele")
public class TeleOpKrab extends OpMode {

    private Gyro gyro = new Gyro();

    private DcMotor intake = null;
    private DcMotorEx flyWheel = null;
    private DcMotor feeder = null;

    private DcMotor wobbleArm = null;
    private Servo wobbleLocker;

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
    private boolean secondB = false;
    private boolean pastA = false;
    private boolean secondA = false;
    private boolean pastLB = false;
    private boolean pastRB = false;
    private boolean engageArm = false;
    private boolean engageLock = false;
    private boolean swap = false;
    private boolean engageRing = false;
    private boolean pastDUp = false;
    private boolean pastDRight = false;
    private boolean pastDDown = false;
    private boolean pastDLeft = false;
    private boolean past2X = false;
    private boolean allOff = false;

    private MecanumDrive drive;
    private Point direction;
    private double power;

    @Override
    public void init() {
        //Get Hardware Map
        drive = new MecanumDrive(hardwareMap, "lmf", "rmf", "lmb", "rmb", 1, 1, 1, 1);

        drive.resetPowerValues();
        gyro.initGyro(hardwareMap);

        telemetry.addData("lmf power", drive.getFrontLeftMotorPower());
        telemetry.addData("rmf power", drive.getFrontRightMotorPower());
        telemetry.addData("lmb power", drive.getBackLeftMotorPower());
        telemetry.addData("rmb power", drive.getBackRightMotorPower());

        intake = hardwareMap.get(DcMotor.class, "intake");
        flyWheel = hardwareMap.get(DcMotorEx.class, "out");
        feeder = hardwareMap.get(DcMotor.class, "feed");
        wobbleArm = hardwareMap.get(DcMotor.class, "wobble" );
        wobbleLocker = hardwareMap.get(Servo.class, "locker");

        //Set Gamepad Deadzone
        gamepad1.setJoystickDeadzone(0.05f);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    public void start(){

    }

    @Override
    public void loop() {
        leftTrigger = gamepad2.left_trigger;
        rightTrigger = gamepad2.right_trigger;

        //Check the multipliers for the motors
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

        //calculate and push the motor powers
        drive.resetPowerValues();

        direction = new Point(-1 * gamepad1.left_stick_x, gamepad1.left_stick_y);
        power = Math.sqrt(gamepad1.left_stick_x * gamepad1.left_stick_x +
                gamepad1.left_stick_y * gamepad1.left_stick_y);
        drive.linearMove(direction, power);
        drive.radialMove(gamepad1.right_stick_x);
        drive.updateMotorPowers();
        telemetry.addData("lmf power", drive.getFrontLeftMotorPower());
        telemetry.addData("rmf power", drive.getFrontRightMotorPower());
        telemetry.addData("lmb power", drive.getBackLeftMotorPower());
        telemetry.addData("rmb power", drive.getBackRightMotorPower());

        if (gamepad2.y && !pastY) {
            if (intake.getPower() == 0) {
                intake.setPower(1);
            }
            else {
                intake.setPower(0);
            }
        }
        pastY = gamepad2.y;

        if (gamepad2.dpad_left && !pastDLeft) {
            if (intake.getPower() == 0) {
                intake.setPower(-1);
            }
            else {
                intake.setPower(0);
            }
        }
        pastDLeft = gamepad2.dpad_left;

        //Turn On Flywheel for high goal
        if (gamepad2.a && !secondA) {
            if (flyWheel.getPower() == 0) {
                flyWheel.setVelocity(-2200);
            }
            else {
                flyWheel.setVelocity(0);
            }
        }
        secondA = gamepad2.a;

        //Set Fly Wheel To Power Shots
        if (gamepad2.right_bumper && !pastRB) {
            if (flyWheel.getPower() == 0) {
                flyWheel.setVelocity(-1900);
            }
            else {
                flyWheel.setVelocity(0);
            }
        }
        pastRB = gamepad2.right_bumper;

        //Turn On Feeder
        if (gamepad2.x && !pastX) {
            if (feeder.getPower() == 0) {
                feeder.setPower(-1);
            }
            else {
                feeder.setPower(0);
            }
        }
        pastX = gamepad2.x;

        //Move wobble arm
        if (gamepad2.left_trigger > 0) {
            while(wobbleArm.getCurrentPosition() < 1300){
                wobbleArm.setPower(gamepad2.left_trigger * .4);
            }
        }
        else if (gamepad2.right_trigger > 0) {
            while(wobbleArm.getCurrentPosition() > 1000){
                wobbleArm.setPower(-gamepad2.right_trigger * .4);
            }
        }
        else {
            wobbleArm.setPower(0);
        }
        //Turn Off Everything
        if(gamepad2.b && !pastB){
            feeder.setPower(0);
            intake.setPower(0);
            flyWheel.setVelocity(0);

        }
        gamepad2.b = pastB;

        //Lock Wobble
        if (gamepad2.left_bumper && !pastLB) {
            if (wobbleLocker.getPosition() == 0) {
                wobbleLocker.setPosition(.89);
            }
            else {
                wobbleLocker.setPosition(0);
            }
        }
        pastLB = gamepad2.left_bumper;


        if(gamepad1.dpad_up && !pastDUp) {
            //drive.orient(0);
        }
        else if (gamepad1.dpad_right && !pastDRight) {
            //drive.orient(1);
        }
        else if (gamepad1.dpad_down && !pastDDown) {
            //drive.orient(2);
        }
        else if (gamepad1.dpad_left && !pastDLeft) {
            //drive.orient(3);
        }
        pastDUp = gamepad1.dpad_up;
        pastDRight = gamepad1.dpad_right;
        pastDDown = gamepad1.dpad_down;
        pastDLeft = gamepad1.dpad_left;

        telemetry.addData("Swap mode", swap);
        telemetry.addData("Front Right Motor Ticks", drive.getFrontRightMotorTicks());
        telemetry.addData("Front Left Motor Ticks", drive.getFrontLeftMotorTicks());
        telemetry.addData("Back Right Motor Ticks", drive.getBackRightMotorTicks());
        telemetry.addData("Back Left Motor Ticks", drive.getBackLeftMotorTicks());
        telemetry.addData("Wobble Arm Encoder", wobbleArm.getCurrentPosition());
        telemetry.addData("Reverse", drive.isReverse());
        telemetry.addData("halfSpeed", drive.isHalfSpeed());
        telemetry.addData("Orientation", drive.getOrientation());
        telemetry.addData("Velocity ", flyWheel.getVelocity());
        telemetry.update();
    }

    public void stop() {
        //Turn Off Motors
        drive.stop();
    }
    /*public int quadrant() {
        double angle = gyro.getAngle() + Values.finalAngle;
        if (angle < 90) {
            return 2;
        }
        else if (angle < 180) {
            return 3;
        }
        else if (angle < 270) {
            return 4;
        }
        else {
            return 1;
        }
    }*/
}

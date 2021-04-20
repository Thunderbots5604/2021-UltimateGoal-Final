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

@TeleOp(name = "SuperTeleOp", group = "Tele")
public class SuperTeleOp extends OpMode {

    private Gyro gyro = new Gyro();

    private DcMotor flMotor = null;
    private DcMotor frMotor = null;
    private DcMotor blMotor = null;
    private DcMotor brMotor = null;

    private DcMotor intake = null;
    private DcMotor flyWheel = null;
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
    private boolean halfSpeed = false;

    private MecanumDrive drive;

    //private boolean feederTimer = false;
    //private

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
        flyWheel = hardwareMap.get(DcMotor.class, "out");
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

        drive.resetPowerValues();
        cheat(gamepad1.left_stick_x, gamepad1.left_stick_y);
        drive.radialMove(gamepad1.right_stick_x * multiplier * 0.75);
        drive.updateMotorPowers();
        telemetry.addData("lmf power", drive.getFrontLeftMotorPower());
        telemetry.addData("rmf power", drive.getFrontRightMotorPower());
        telemetry.addData("lmb power", drive.getBackLeftMotorPower());
        telemetry.addData("rmb power", drive.getBackRightMotorPower());

        //Reverse Toggle
        if(!pastX && gamepad1.x){
            reverse = !reverse;
            multiplier *= -1;
        }
        pastX = gamepad1.x;

        //Halfspeed Toggle
        if(gamepad1.a == false && pastA == true){
            halfSpeed = !halfSpeed;
            if(halfSpeed == true){
                multiplier *= 0.5;

            }
            else{
                multiplier *= 2;
            }
        }
        pastA = gamepad1.a;

        if (gamepad2.y && !pastY) {
            if (intake.getPower() == 0) {
                intake.setPower(1);
            }
            else {
                intake.setPower(0);
            }
        }
        pastY = gamepad2.y;
        if (gamepad2.a && !secondA) {
            if (flyWheel.getPower() == 0) {
                flyWheel.setPower(-1);
            }
            else {
                flyWheel.setPower(0);
            }
        }
        secondA = gamepad2.a;
        if (gamepad2.right_bumper && !pastRB) {
            if (feeder.getPower() == 0) {
                feeder.setPower(-1);
                //feederTimer = true;
                //feederTimerInitial = runtime.Milliseconds();
                flyWheel.setPower(-1);
            }
            else {
                feeder.setPower(0);
            }
        }
        pastRB = gamepad2.right_bumper;
        if (gamepad2.left_trigger > 0) {
            wobbleArm.setPower(gamepad2.left_trigger);
        }
        else if (gamepad2.right_trigger > 0) {
            wobbleArm.setPower(-gamepad2.right_trigger);
        }
        else {
            wobbleArm.setPower(0);
        }

        if (gamepad2.left_bumper && pastLB) {
            if (wobbleLocker.getPosition() == 0) {
                wobbleLocker.setPosition(.89);
            }
            else {
                wobbleLocker.setPosition(0);
            }
        }
        pastLB = gamepad2.left_bumper;
        if(gamepad2.back) {
            flMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            blMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            brMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            flMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            frMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            blMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            brMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        /*telemetry.addData("Swap mode: ", swap);
        telemetry.addData("Front Right Motor = ", frMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", flMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", brMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", frMotor.getCurrentPosition());*/
        telemetry.addData("Wobble Arm Encoder = ", wobbleArm.getCurrentPosition());
        telemetry.addData("Reverse = ", reverse);
        telemetry.addData("halfSpeed = ", halfSpeed);
        telemetry.update();
    }

    private void cheat(double x, double y) {
        if (x == 0 && y == 0) {
            return;
        }
        Point direction = new Point(x, y);
        double power = Math.sqrt(x*x + y*y) * multiplier;
        drive.linearMove(direction, power);
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

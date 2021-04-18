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
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
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

@TeleOp(name = "ShellTeleOp", group = "Tele")
public class ShellTeleOp extends OpMode {

    private DcMotor flMotor = null;
    private DcMotor frMotor = null;
    private DcMotor blMotor = null;
    private DcMotor brMotor = null;
    private DcMotor liftL = null;
    private DcMotor liftR = null;
    private DcMotor wMotor;
    private DcMotorEx flyWheel;

    private Servo wobbleHigh;
    private Servo wobbleLow;
    private Servo WobbleArm;
    private Servo RingArm;
    private Servo feedArm;

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
    private boolean grab = false;
    private boolean engageRing = false;
    private boolean halfSpeed = false;
    private boolean shot = false;

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

        flyWheel = hardwareMap.get(DcMotorEx.class, "flyWheel");

        wMotor = hardwareMap.get(DcMotor.class, "wobbleMotor");

        wobbleHigh = hardwareMap.get(Servo.class, "wh" );
        wobbleLow = hardwareMap.get(Servo.class, "wl");

        RingArm = hardwareMap.get(Servo.class, "RingGrabber");
        WobbleArm = hardwareMap.get(Servo.class, "WobbleArm");
        feedArm = hardwareMap.get(Servo.class, "feedArm");

        frontColor = hardwareMap.get(ColorSensor.class, "fc");
        backColor = hardwareMap.get(ColorSensor.class, "bc");

        liftL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        liftL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        flyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        //Set Gamepad Deadzone
        gamepad1.setJoystickDeadzone(0.05f);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        drive.resetPowerValues();
        cheat(gamepad1.left_stick_x, gamepad1.left_stick_y);
        drive.radialMove(gamepad1.right_stick_x * multiplier * 0.5);
        drive.updateMotorPowers();
        telemetry.addData("lmf power", drive.getFrontLeftMotorPower());
        telemetry.addData("rmf power", drive.getFrontRightMotorPower());
        telemetry.addData("lmb power", drive.getBackLeftMotorPower());
        telemetry.addData("rmb power", drive.getBackRightMotorPower());

        flyWheel.setVelocity(-2000);


        //Speed Toggle
        if (gamepad1.left_trigger > 0.8){
            multiplier = 0.5;

        } else {
            multiplier = 1;
        }

        if(gamepad1.a){
            feedArm.setPosition(1);
            shot = true;
        } else {
            feedArm.setPosition(0);
            shot = false;
        }
        if(gamepad2.right_trigger > 0){
            wMotor.setPower(gamepad2.right_trigger / 2);

        } else if (gamepad2.left_trigger > 0){
            wMotor.setPower(-gamepad2.left_trigger / 2);
        } else {
            wMotor.setPower(0);
        }

        if(!pastY && gamepad2.y){
            grab = !grab;
            if(grab){
                wobbleHigh.setPosition(0.85);
                //wobbleLow.setPosition(0.85);

            }
            else{
                wobbleHigh.setPosition(0.5);
                //wobbleLow.setPosition(0.5);
            }
        }
        pastY = gamepad2.y;

        telemetry.addData("Shoot = ", shot);
        telemetry.addData("LiftMech = ", liftL.getCurrentPosition());
        telemetry.addData("Arm Servo = ", WobbleArm.getPosition());
        telemetry.addData("Ring Arm = ", ring);
        telemetry.addData("WobbleLocker =", grab);
        telemetry.update();
    }
    private void cheat(double x, double y) {
        if (x == 0 && y == 0) {
            return;
        }
        Point direction = new Point(x, y);
        double power = Math.sqrt(x*x + y*y) * multiplier * 0.5;
        drive.linearMove(direction, power);
    }
  /*  private void shoot(){
        if(gamepad1.a){
            feedArm.setPosition(1);
            shot = true;
        } else {
            feedArm.setPosition(0);
            shot = false;
        }
    }*/

    @Override
    public void stop() {
        drive.stop();
    }
} 
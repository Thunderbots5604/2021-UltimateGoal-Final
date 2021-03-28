
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "TeleOp", group = "Tele")
public class GoodTeleOp extends OpMode {

    private DcMotor flMotor = null;
    private DcMotor frMotor = null;
    private DcMotor blMotor = null;
    private DcMotor brMotor = null;
    private DcMotor LiftL = null;
    private DcMotor LiftR = null;


    private int directionMecanum;
    private int directionTurn;
    private boolean halfSpeed;
    private boolean reverse;
    private double maneuverMultiplier;
    private double angle;

    // mecanumPowers is [FRBL, FLBR]
    private double[] mecanumPowers;
    // turnDrivePowers is [FLBL, FRBR]
    private double[] turnDrivePowers;
    // driveMultipliers is [mecanum, turn]
    private double[] driveMultipliers;
    // motorPowers is [FL, FR, BL, BR]
    private double[] motorPowers;

    private boolean xButtonPrevious;
    private boolean yButtonPrevious;
    private boolean rightStickButtonPrevious;
    private double p1LeftStickXPrevious;
    private double p1LeftStickYPrevious;
    private double p1RightStickXPrevious;
    private boolean xButtonCurrent;
    private boolean yButtonCurrent;
    private boolean rightStickButtonCurrent;
    private double p1LeftStickXCurrent;
    private double p1LeftStickYCurrent;
    private double p1RightStickXCurrent;

    private Servo WobbleLocker;
    private Servo WobbleArm;
    private Servo RingArm;

    private double powerFRBL = 0;
    private double powerFLBR = 0;
    private double multiplier = 1;

    @Override
    public void init() {

        //Get Hardware Map
        flMotor = hardwareMap.get(DcMotor.class, "lmf" );
        frMotor = hardwareMap.get(DcMotor.class, "rmf" );
        blMotor = hardwareMap.get(DcMotor.class, "lmb" );
        brMotor = hardwareMap.get(DcMotor.class, "rmb" );
        LiftL = hardwareMap.get(DcMotor.class, "LiftMechL");
        LiftR = hardwareMap.get(DcMotor.class, "LiftMechR");

        WobbleLocker = hardwareMap.get(Servo.class, "WobbleGoalLocker" );
        RingArm = hardwareMap.get(Servo.class, "RingGrabber");
        WobbleArm = hardwareMap.get(Servo.class, "WobbleGoalArm");

        this.halfSpeed = false;
        this.reverse = false;
        this.mecanumPowers = new double[2];
        this.turnDrivePowers = new double[2];
        this.driveMultipliers = new double[2];
        this.motorPowers = new double[4];
        this.maneuverMultiplier = 1;
        this.directionMecanum = 0;
        this.directionTurn = 0;
        xButtonPrevious = false;
        yButtonPrevious = false;
        rightStickButtonPrevious = false;
        p1LeftStickXPrevious = 0;
        p1LeftStickYPrevious = 0;
        p1RightStickXPrevious = 0;
        xButtonCurrent = gamepad1.x || gamepad2.x;
        yButtonCurrent = gamepad1.y || gamepad2.y;
        p1LeftStickXCurrent = gamepad1.left_stick_x;
        p1LeftStickYCurrent = gamepad1.left_stick_y;
        p1RightStickXCurrent = gamepad1.right_stick_x;
        rightStickButtonCurrent = gamepad1.right_stick_button || gamepad2.right_stick_button;

        //Reverse Motor Directions to Drive Straight
        flMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        blMotor.setDirection(DcMotorSimple.Direction.REVERSE);


        //Set Gamepad Deadzone
        gamepad1.setJoystickDeadzone(0.1f);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

    }

    @Override
    public void loop() {
        telemetry.addData("Status", "Running");
        telemetry.update();

        if (gamepad1.left_stick_y != 0 || gamepad1.left_stick_x != 0) {
            //FR BL pair
            powerFRBL = (gamepad1.left_stick_x + gamepad1.left_stick_y) / Math.sqrt(2);
            blMotor.setPower(multiplier * powerFRBL);
            frMotor.setPower(multiplier * powerFRBL);
            //FL BR pair
            powerFLBR = (gamepad1.left_stick_y - gamepad1.left_stick_x) / Math.sqrt(2);
            flMotor.setPower(multiplier * powerFLBR);
            brMotor.setPower(multiplier * powerFLBR);

        }
        else if (gamepad1.left_stick_y == 0 && gamepad1.right_stick_x != 0) {
            flMotor.setPower(-Math.abs(multiplier) * gamepad1.right_stick_x * .7);
            blMotor.setPower(-Math.abs(multiplier) * gamepad1.right_stick_x * .7);
            frMotor.setPower(Math.abs(multiplier) * gamepad1.right_stick_x * .7);
            brMotor.setPower(Math.abs(multiplier) * gamepad1.right_stick_x * .7);
            telemetry.addData("Right stick", gamepad1.right_stick_x);
        }
        else {
            flMotor.setPower(0);
            frMotor.setPower(0);
            blMotor.setPower(0);
            brMotor.setPower(0);
        }




        if(gamepad1.b) {
            flMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            blMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            brMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            flMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            frMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            blMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            brMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }

        telemetry.addData("Front Right Motor = ", frMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", flMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", brMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", frMotor.getCurrentPosition());
        telemetry.update();



        if(gamepad1.y){
            LiftL.setPower(0.5);
            LiftR.setPower(0.5);
        }

        else if(gamepad1.x){
            LiftL.setPower(-0.5);
            LiftR.setPower(-0.5);
        }
        else{
            LiftL.setPower(0);
            LiftR.setPower(0);
        }




    }




    public void stop() {
        //Turn Off Motors
        flMotor.setPower(0);
        frMotor.setPower(0);
        blMotor.setPower(0);
        brMotor.setPower(0);
    }
}

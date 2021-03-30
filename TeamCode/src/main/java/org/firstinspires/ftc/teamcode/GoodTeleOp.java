
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

    private Servo WobbleLocker;
    private Servo WobbleArm;
    private Servo RingArm;

    private double powerFRBL = 0;
    private double powerFLBR = 0;
    private double multiplier = 1;

    private boolean engageArm = false;
    private boolean engageLock = false;
    private boolean swap = false;

    @Override
    public void init() {

        //Get Hardware Map
        flMotor = hardwareMap.get(DcMotor.class, "lmf" );
        frMotor = hardwareMap.get(DcMotor.class, "rmf" );
        blMotor = hardwareMap.get(DcMotor.class, "lmb" );
        brMotor = hardwareMap.get(DcMotor.class, "rmb" );
        LiftL = hardwareMap.get(DcMotor.class, "LiftMechL");
        LiftR = hardwareMap.get(DcMotor.class, "LiftMechR");

        WobbleLocker = hardwareMap.get(Servo.class, "WobbleLocker" );
        RingArm = hardwareMap.get(Servo.class, "RingGrabber");
        WobbleArm = hardwareMap.get(Servo.class, "WobbleArm");

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

        if (gamepad1.right_bumper && gamepad1.b) {
            if (engageLock) {
                WobbleLocker.setPosition(0.6);
                engageLock = false;
            } else {
                WobbleLocker.setPosition(0.5);
                engageLock = true;
            }
        }

        if (gamepad1.b) {
            if (engageArm) {
                WobbleArm.setPosition(0.6);
                engageArm = false;
            } else {
                WobbleArm.setPosition(0.5);
                engageArm = true;
            }
        }

        if (gamepad1.back) {
            if (swap) {
                swap = false;
            } else {
                swap = true;
            }
        }

        if(gamepad2.b) {
            flMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            blMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            brMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            flMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            frMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            blMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            brMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        telemetry.addData("Swap mode: ", Boolean.toString(swap));

        telemetry.addData("Front Right Motor = ", frMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", flMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", brMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", frMotor.getCurrentPosition());
        telemetry.update();
    }

    public void stop() {
        //Turn Off Motors
        flMotor.setPower(0);
        frMotor.setPower(0);
        blMotor.setPower(0);
        brMotor.setPower(0);
    }
}

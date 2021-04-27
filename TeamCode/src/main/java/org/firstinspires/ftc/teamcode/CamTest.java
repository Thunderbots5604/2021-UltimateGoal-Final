
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

@TeleOp(name = "Cam Test", group = "Tele")
public class CamTest extends OpMode {

    private DcMotor flMotor = null;
    private DcMotor frMotor = null;
    private DcMotor blMotor = null;
    private DcMotor brMotor = null;

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
    private boolean secondB = false;
    private boolean pastA = false;
    private boolean secondA = false;
    private boolean pastLB = false;
    private boolean engageArm = false;
    private boolean engageLock = false;
    private boolean swap = false;
    private boolean engageRing = false;
    private boolean halfSpeed = false;

    private Cam cam = new Cam(telemetry, hardwareMap);
    private double[] coords = {0, 0, 0};

    @Override
    public void init() {
        telemetry.addData("initializing", null);
        telemetry.update();

        cam.initVuforia(hardwareMap);

        //Get Hardware Map
        flMotor = hardwareMap.get(DcMotor.class, "lmf" );
        frMotor = hardwareMap.get(DcMotor.class, "rmf" );
        blMotor = hardwareMap.get(DcMotor.class, "lmb" );
        brMotor = hardwareMap.get(DcMotor.class, "rmb" );

        //Reverse Motor Directions to Drive Straight
        flMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        blMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        //Set Gamepad Deadzone
        gamepad1.setJoystickDeadzone(0.05f);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    public void start(){

    }

    @Override
    public void loop() {

        cam.getCoords();
        coords = Values.currentCoords;
        leftTrigger = gamepad2.left_trigger;
        rightTrigger = gamepad2.right_trigger;


        if (gamepad1.left_stick_y != 0 || gamepad1.left_stick_x != 0) {
            //FR BL pair
            powerFRBL = (gamepad1.left_stick_x + gamepad1.left_stick_y) / Math.sqrt(2);
            blMotor.setPower(multiplier * powerFRBL * 0.75);
            frMotor.setPower(multiplier * powerFRBL * 0.75);
            //FL BR pair
            powerFLBR = (gamepad1.left_stick_y - gamepad1.left_stick_x) / Math.sqrt(2);
            flMotor.setPower(multiplier * powerFLBR * 0.75);
            brMotor.setPower(multiplier * powerFLBR* 0.75);

        }
        else if (gamepad1.left_stick_y == 0 && gamepad1.right_stick_x != 0) {
            flMotor.setPower(-Math.abs(multiplier) * gamepad1.right_stick_x * .7);
            blMotor.setPower(-Math.abs(multiplier) * gamepad1.right_stick_x * .7);
            frMotor.setPower(Math.abs(multiplier) * gamepad1.right_stick_x * .7);
            brMotor.setPower(Math.abs(multiplier) * gamepad1.right_stick_x * .7);
            telemetry.addData("Right stick", gamepad1.right_stick_x);
        }
        //Second Drive at Quarter Speed
        else if (gamepad2.left_stick_y != 0 || gamepad2.left_stick_x != 0) {
            //FR BL pair
            powerFRBL = (gamepad2.left_stick_x + gamepad2.left_stick_y) / Math.sqrt(2);
            blMotor.setPower(multiplier * powerFRBL * quarterSpeed);
            frMotor.setPower(multiplier * powerFRBL * quarterSpeed);
            //FL BR pair
            powerFLBR = (gamepad2.left_stick_y - gamepad2.left_stick_x) / Math.sqrt(2);
            flMotor.setPower(multiplier * powerFLBR * quarterSpeed);
            brMotor.setPower(multiplier * powerFLBR * quarterSpeed);

        }
        else if (gamepad2.left_stick_y == 0 && gamepad2.right_stick_x != 0) {
            flMotor.setPower(-Math.abs(multiplier) * gamepad2.right_stick_x * .7 * quarterSpeed);
            blMotor.setPower(-Math.abs(multiplier) * gamepad2.right_stick_x * .7 * quarterSpeed);
            frMotor.setPower(Math.abs(multiplier) * gamepad2.right_stick_x * .7 * quarterSpeed);
            brMotor.setPower(Math.abs(multiplier) * gamepad2.right_stick_x * .7 * quarterSpeed);
            telemetry.addData("Right stick", gamepad2.right_stick_x);
        }
        else {
            flMotor.setPower(0);
            frMotor.setPower(0);
            blMotor.setPower(0);
            brMotor.setPower(0);
        }

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

        telemetry.addData("Front Right Motor = ", frMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", flMotor.getCurrentPosition());
        telemetry.addData("Back Right Motor = ", brMotor.getCurrentPosition());
        telemetry.addData("Back Left Motor = ", blMotor.getCurrentPosition());
        telemetry.addData("x: ", coords[0]);
        telemetry.addData("y: ", coords[1]);
        telemetry.addData("Angle: ", coords[2]);
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

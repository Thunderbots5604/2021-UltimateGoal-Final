
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
    private DcMotor liftL = null;
    private DcMotor liftR = null;

    private Servo WobbleLocker;
    private Servo WobbleArm;
    private Servo RingArm;

    private double powerFRBL = 0;
    private double powerFLBR = 0;
    private double multiplier = 1;
    private double leftTrigger = 0;
    private double rightTrigger = 0;
    
    private boolean reverse = false;
    private boolean ring = false;
    private boolean pastX = false;
    private boolean pastY = false;
    private boolean pastB = false;
    private boolean pastA = false;
    private boolean pastLB = false;
    private boolean engageArm = false;
    private boolean engageLock = false;
    private boolean swap = false;
    private boolean engageRing = false;
    private boolean halfSpeed = false;
    
    


    @Override
    public void init() {
        
        //test
        telemetry.addData("test", null);
        telemetry.update();

        //Get Hardware Map
        flMotor = hardwareMap.get(DcMotor.class, "lmf" );
        frMotor = hardwareMap.get(DcMotor.class, "rmf" );
        blMotor = hardwareMap.get(DcMotor.class, "lmb" );
        brMotor = hardwareMap.get(DcMotor.class, "rmb" );
        liftL = hardwareMap.get(DcMotor.class, "LiftMechL");
        liftR = hardwareMap.get(DcMotor.class, "LiftMechR");

        WobbleLocker = hardwareMap.get(Servo.class, "WobbleLocker" );
        RingArm = hardwareMap.get(Servo.class, "RingGrabber");
        WobbleArm = hardwareMap.get(Servo.class, "WobbleArm");

        //Reverse Motor Directions to Drive Straight
        flMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        blMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        liftL.setDirection(DcMotorSimple.Direction.REVERSE);
        
        liftL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        liftL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        
        
        //Set Gamepad Deadzone
        gamepad1.setJoystickDeadzone(0.1f);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        leftTrigger = gamepad1.left_trigger;
        rightTrigger = gamepad1.right_trigger;
        
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
        
        if(!pastB && gamepad2.b){
            reverse = !reverse;
            multiplier *= -1;
        }
        
        pastB = gamepad2.b;

        if(leftTrigger > 0 && liftL.getCurrentPosition() < 10){
            liftL.setPower(leftTrigger);
            liftR.setPower(leftTrigger);
            
        }
        
        if(gamepad2.left_bumper == false && pastLB == true){
            halfSpeed = !halfSpeed;
            if(halfSpeed == true){
                multiplier *= 0.5;
                
            }
            else{
                multiplier *= 2;
            }
        }
      pastLB = gamepad2.left_bumper;
        if(rightTrigger > 0){
            liftL.setPower(-rightTrigger);
            liftR.setPower(-rightTrigger);
        }
        
        if (gamepad1.left_bumper){
            liftL.setTargetPosition(0);
            liftR.setTargetPosition(0);
            liftR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftL.setPower(.5);
            liftR.setPower(.5);
        }
        if (gamepad1.right_bumper){
            liftL.setTargetPosition(-240);
            liftR.setTargetPosition(-240);
            liftR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftL.setPower(.5);
            liftR.setPower(.5);
        }
        
        if(gamepad1.x && !pastX){
            engageLock = !engageLock;
            if(engageLock){
                WobbleLocker.setPosition(0.5);
                
            }
            else{
                WobbleLocker.setPosition(0.4);
            }
        }
        pastX = gamepad1.x;
        
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
         if(gamepad1.a && !pastA){
            ring = !ring;
            if(ring){
                RingArm.setPosition(0);
            }
            else{
                RingArm.setPosition(-0.5);
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
            liftL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            liftR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        }
        
        telemetry.addData("Swap mode: ", swap);
        telemetry.addData("Front Right Motor = ", frMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", flMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", brMotor.getCurrentPosition());
        telemetry.addData("Front Left Motor = ", frMotor.getCurrentPosition());
        telemetry.addData("LiftMech = ", liftL.getCurrentPosition());
        telemetry.addData("Arm Servo = ", WobbleArm.getPosition() );
        telemetry.addData("Reverse = ", reverse);
        telemetry.addData("halfSpeed = ", halfSpeed );
        telemetry.addData("Ring Arm = ", ring );
        telemetry.addData("WobbleLocker", engageLock);
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

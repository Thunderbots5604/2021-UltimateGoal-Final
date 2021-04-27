
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Autonomous(name="Other Motors", group="")
@Disabled

public class Others extends LinearOpMode {

    public Gyro gyro = new Gyro();
    public ElapsedTime time = new ElapsedTime();

    //Declare Motors and Servos
    private DcMotor intake = null;
    private DcMotorEx flyWheel = null;
    private DcMotor feeder = null;
    private DcMotor wobbleArm;

    private Servo wobbleLocker = null;
    private Servo camPlatform;

    private ColorSensor backColor;

    //Moving stuff between classes
    private Telemetry telemetry;

    private ElapsedTime runtime = new ElapsedTime();

    public Others(Telemetry telemetry) {
        this.telemetry = telemetry;
    }


    @Override
    public void runOpMode() {}
    public void initMotors(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, "intake");
        flyWheel = hardwareMap.get(DcMotorEx.class, "out");
        feeder = hardwareMap.get(DcMotor.class, "feed");
        wobbleArm = hardwareMap.get(DcMotor.class, "wobble");

        camPlatform = hardwareMap.get(Servo.class, "camplat");
        wobbleLocker = hardwareMap.get(Servo.class, "locker");

        backColor = hardwareMap.get(ColorSensor.class, "bc");

        wobbleLocker.setPosition(.89);
        //.8 for camplatform is looking left ish
        camPlatform.setPosition(1);
    }
    public void dropArm() {
        reset();
        while (wobbleArm.getCurrentPosition() < 1300) {
            wobbleArm.setPower(.5);
        }
        wobbleArm.setPower(0);
        wobbleLocker.setPosition(0);
    }
    public void resetArm() {
        reset();
        while (wobbleArm.getCurrentPosition() > -1000) {
            wobbleArm.setPower(-.5);
        }
        wobbleArm.setPower(0);
    }
    public void fireRing(int rings) {
        while (rings > 0) {
            time.reset();
            feeder.setPower(0);
            while (getShootSpeed() > -2200 && time.milliseconds() < 3000) {
                telemetry.addData("Speed: ", getShootSpeed());
                telemetry.update();
            }
            sleep(500);
            feeder.setPower(-1);
            while (getShootSpeed() < -2000 && time.milliseconds() < 3000) {}
            rings--;
        }
        sleep(500);
        feeder.setPower(0);
    }
    public void getRing(){
        intake.setPower(1);
    }
    public void ringLG() {
        intake.setPower(0);
        feeder.setPower(0);
    }
    public void shootPower(double power) {
        flyWheel.setVelocity(power);
    }
    public double getShootSpeed() {
        return flyWheel.getVelocity();
    }
    public int[] getReading(ColorSensor colorsensor) {
        //outputs red, green, blue, luminosity, hue
        int[] reading = new int[5];
        reading[0] = colorsensor.red();
        reading[1] = colorsensor.green();
        reading[2] = colorsensor.blue();
        reading[3] = colorsensor.alpha();
        reading[4] = colorsensor.argb();
        return reading;
    }
    public void testReading() {
        int[] reading = new int[5];
        while (true) {
            reading = getReading(backColor);
            telemetry.addData("R ", reading[0]);
            telemetry.addData("G ", reading[1]);
            telemetry.addData("B ", reading[2]);
            telemetry.addData("L ", reading[3]);
            telemetry.addData("A ", reading[4]);
            telemetry.update();
        }
    }
    public void rotateCamPlatform(double angle) {
        camPlatform.setPosition(angle);
        if (camPlatform.getPosition() == 0) {
            Cam.rotated = false;
        }
        else {
            Cam.rotated = true;
        }
    }
    public void reset() {
        wobbleArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wobbleArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}

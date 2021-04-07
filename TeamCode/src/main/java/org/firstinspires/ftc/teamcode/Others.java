
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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

    //Declare Motors
    private DcMotor LiftL = null;
    private DcMotor LiftR = null;

    //Declare Servos
    private Servo WobbleLocker;
    private Servo WobbleArm;
    private Servo RingArm;

    private ColorSensor frontColor;
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
        LiftL = hardwareMap.get(DcMotor.class, "LiftMechL");
        LiftR = hardwareMap.get(DcMotor.class, "LiftMechR");
        LiftR.setDirection(DcMotorSimple.Direction.REVERSE);

        WobbleLocker = hardwareMap.get(Servo.class, "WobbleLocker");
        RingArm = hardwareMap.get(Servo.class, "RingGrabber");
        WobbleArm = hardwareMap.get(Servo.class, "WobbleArm");

        frontColor = hardwareMap.get(ColorSensor.class, "fc");
        backColor = hardwareMap.get(ColorSensor.class, "bc");

    }
    public void lift() {

    }
    public void dropArm() {
        RingArm.setPosition(0.61);
        sleep(1200);
    }
    public void resetArm() {
        RingArm.setPosition(0);
    }
    public void fireRing() {

    }
    public void getRing(){

    }
    public void ringLG() {

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
}
